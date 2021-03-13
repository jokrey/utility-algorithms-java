package jokrey.utilities.ring_buffer;

import jokrey.utilities.bitsandbytes.BitHelper;
import jokrey.utilities.encoder.as_union.li.LIPosition;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.simple.data_structure.ExtendedIterator;
import jokrey.utilities.simple.data_structure.queue.Queue;
import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A capacitated fifo queue on storage
 * or
 * A ring buffer on storage
 *
 * Has atomicity guarantees for append, read and delete - as long as the underlying storage has them also for single ops
 *
 * Assumptions:
 *  Max bytes are readily available. The buffer is not optimized towards setting the content below max, after using it.
 *  Occasionally it has to, but usually it does not
 *
 * todo: reverse iteration (
 *    - done by adding a reverse li(leading_li must be at the end) to the end (double linked)
 *    - start point of reverse iteration is drStart(because just before that will be the reverse-li)
 *  )
 *  todo delete latest
 */
public class VarSizedRingBuffer implements Queue<byte[]> {
    public final long max;

    protected final TransparentBytesStorage storage;

    protected final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * @param storage must be editable,
     *                for atomicity of this buffer it must support atomicity of
     *                {@link TransparentBytesStorage#set(long, byte[]...)} and {@link TransparentBytesStorage#delete(long, long)} and {@link TransparentBytesStorage#setContent(Object)}
     * @param max max index at which data will be written to underlying storage(appends will wrap and overwrite)
     * @throws IllegalArgumentException if max < {@link VarSizedRingBuffer#START} or max < storage.contentSize()
     */
    public VarSizedRingBuffer(TransparentBytesStorage storage, long max) {
        if(max < START) throw new IllegalArgumentException("max cannot be smaller than header("+START+" bytes)");
        if(max < storage.contentSize()) throw new IllegalArgumentException("max cannot be smaller than current storage size - truncate first");
        this.max = max;
        this.storage = storage;

        initHeader();
    }

    /**
     * Append the given element to the end of this buffer,
     *   the new element will be overwritten later than any other elements previously in the buffer
     * @return false if the element was too large to be added, true otherwise
     * @throws IllegalArgumentException if given element does not fit buffer(i.e. max < START + li(e) + |e|)
     * @throws IllegalStateException if the underlying data is corrupt and does not represent a vsrb
     */
    public boolean append(byte[] e) {
        rwLock.writeLock().lock();
        try {
            byte[] li = LIbae.generateLI(e.length);
            int lieSize = li.length + e.length;
            long newDrEnd;

            long nextWriteStart = drStart;//start writing at dirty region
            long nextWriteEnd = nextWriteStart + lieSize;
            if (nextWriteEnd > max) {//if our write has to wrap
                nextWriteStart = START;
                nextWriteEnd = nextWriteStart + lieSize;
                if(nextWriteEnd > max)//cannot fit
                    return false;

                truncateToDirtyRegionStart(); //truncate to previous dirty region start, because that is the last written location

                if(Math.max(drEnd, nextWriteEnd) < drStart) //if we were in an appending mode before, but drEnd was earlier (indicates crash, or deleted first element)
                    newDrEnd = drEnd; //then the dirty region cannot end earlier than drEnd - can end later,
                else
                    newDrEnd = searchNextLIEndAfter(START, nextWriteEnd); //search from start for next li end after writeEnd
            } else if (drEnd > nextWriteEnd) { // write is an overwrite, but fits the dirty region
                newDrEnd = drEnd;
            } else { //write is an overwrite, and the currently element does not fit the dirty region
                if(drEnd < drStart) //if drEnd < drStart -> dirty region = [START, drEnd] && drStart==contentSize()
                    newDrEnd = drEnd;//dr start will be written on commit (or reset if this change does not happen) - but drEnd shall not change
                else
                    newDrEnd = searchNextLIEndAfter(drEnd, nextWriteEnd);//we know that at drEnd there is an li - the earliest element we are overwriting now
            }



            preCommit(newDrEnd, nextWriteStart, nextWriteEnd);
            writeElem(nextWriteStart, li, e);//write element with length indicator - can fail at anypoint internally
            commit(nextWriteEnd, newDrEnd);//commit write element
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    protected long searchNextLIEndAfter(long startAt, long minResult) {
        if(minResult >= storage.contentSize()) {//this write will append
            return minResult;
        } else {
            while (startAt < minResult) { // we jump from li to li, until we encompass the nextWriteEnd
                long[] liBounds = readLIBoundsAt(startAt);
                if (liBounds == null) {//if we cannot read any the next li (for example eof, or writing for the first time)
                    return minResult;
                } else {
                    startAt = liBounds[1];
                }
            }
        }
        return startAt;
    }

    /**
     * @return whether anything was deleted (only nothing deleted if empty)
     */
    public boolean deleteFirst() {
        rwLock.writeLock().lock();
        try {
            if (drStart == START && drEnd == START) return false; //cannot delete empty

            long oldContentSize = storage.contentSize();
            long newDrEnd = drEnd;
            long newDrStart = drStart;
            if (drEnd == oldContentSize) {//if dirty region ends at old content size -> WRAP(i.e. delete at the start)
                //on crash between truncate and writeCole, or if never wrapped write, or if dirtyRegion empty and contentSize() == max
                newDrEnd = START;

                if (drEnd > drStart) { //only if nothing deleted yet, if drStart is at content size, we want to write more stuff before overwriting
                    truncateToDirtyRegionStart();//we just wrapped, we need to set content size to oldDrStart, because we don't want to read over it every
                    //if we crash between here and writePost, then we do not delete and:
                    //  -> after recover drStart==drEnd==contentSize()
                    //     on read: drEnd==contentSize() -> read from start
                    //     on write: drEnd=contentSize() -> drStart=contentSize() -> append at contentSize
                    //        which is correct, since earliest was not deleted
                    newDrStart = START;
                }
            }

            long[] liBounds = readLIBoundsAt(newDrEnd);
            newDrEnd = (liBounds == null ? storage.contentSize() : liBounds[1]);

            if ((drEnd == newDrEnd || drEnd == oldContentSize) && drStart == newDrStart && (drStart < storage.contentSize()))
                return false; //based on the above checks we know that we cannot delete anything, since everything has been deleted

            if (newDrEnd == storage.contentSize() && (newDrStart == START || newDrStart == storage.contentSize())) {
                //if the entire buffer is now a dirty region, truncate everything
                clear();
            } else {
                commit(newDrStart, newDrEnd); //increase dirty region by 1 li element
            }
            return true;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Clears this buffer, might change the size of the underlying storage.
     */
    public void clear() {
        rwLock.writeLock().lock();
        try {
            drStart = drEnd = START;
            storage.setContent(ByteArrayStorage.getConcatenated(
                BitHelper.getBytes((long) START), BitHelper.getBytes((long) START),
                BitHelper.getBytes(-1L), BitHelper.getBytes(-1L)
            ));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * TODO - this is not thread safe...
     *      Any single read operation will be read locked.
     *      BUT, the pointer location might be invalid as heck, if we come to it again after a write.
     *      This is NOT necessarily even detected (could be valid(ish) coincidentally -> return junk data)
     * @return an extended iterator capable of next, hasNext and skip (delete is not supported)
     */
    public ExtendedIterator<byte[]> iterator() {
        rwLock.readLock().lock();
        try {
            // This iterator will read from dirty region end, wrapping once around until dirty region start
            // if dirty region end == dirty region start it will iterate in a circle, from that dirty region

            long oldContentSize = storage.contentSize();//NOT THREAD SAFE ANYWAYS - this make it actually a little more safe (though insufficiently

            LIbae decoder = new LIbae(storage);
            AtomicBoolean wrapReadAllowed = new AtomicBoolean(true);
            LIPosition iter = new LIPosition(drEnd);
            return new ExtendedIterator<byte[]>() {
                boolean readHasAlreadyWrapped() {
                    return !wrapReadAllowed.get();
                }
                boolean dirtyRegionEndsAtStart() {
                    return drStart == oldContentSize && drStart > drEnd;
                }
                boolean reachedEnd() {
                    long virtualDirtyRegionStart = dirtyRegionEndsAtStart() ? VarSizedRingBuffer.START : drStart;
                    return readHasAlreadyWrapped() && iter.pointer == virtualDirtyRegionStart;
                }
                void wrap() {
                    wrapReadAllowed.set(false);
                    iter.pointer = START;
                }

                @Override public boolean hasNext () {
                    rwLock.readLock().lock();
                    try {
                        if(reachedEnd()) return false;
                        if(iter.hasNext(storage)) return true;
                        if(readHasAlreadyWrapped()) return false;
                        return !dirtyRegionEndsAtStart() && new LIPosition(START).hasNext(storage);
                    } catch (StorageSystemException e) {
                        throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+"). Kinda indicates no such element");
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }

                @Override public byte[] next_or_null() {
                    rwLock.readLock().lock();
                    try {
                        if (reachedEnd()) return null;

                        byte[] next = decoder.decode(iter);
                        if(next != null) return next;

                        if(readHasAlreadyWrapped()) return null;

                        wrap();
                        return next_or_null();
                    } catch (StorageSystemException e) {
                        throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+"). Kinda indicates no such element");
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }

                @Override public void skip() {
                    rwLock.readLock().lock();
                    try {
                        if (reachedEnd()) throw new NoSuchElementException("No next element");

                        long decoded = decoder.skipEntry(iter);
                        if(decoded >= 0) return;

                        if(readHasAlreadyWrapped()) throw new NoSuchElementException("No next element");

                        wrap();
                        skip();
                    } catch (StorageSystemException e) {
                        throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+"). Kinda indicates no such element");
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }
            };
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * TODO - requires reverse link li
     * @return
     */
    public ExtendedIterator<byte[]> reverseIterator() {
        throw new UnsupportedOperationException();
    }





    //LI STUFF:

    public long[] readLIBoundsAt(long pointer) {
        long content_size = storage.contentSize();
        if(pointer+1>content_size)
            return null;
        byte[] cache = storage.sub(pointer, pointer+9); //cache maximum number of required bytes. (to minimize possibly slow sub calls)
        return LIbae.get_next_li_bounds(cache, 0, pointer, content_size);
    }

    //HEADER STUFF:

    public static final int START = 32;

    //last write location
    protected long drStart = -1;//dirty region start
    //current overwritten libae end
    protected long drEnd = -1;//dirty region end

    protected void initHeader() {
        if (storage.contentSize() <= START) {
            commit(START, START);
        } else {
            byte[] headerBytes = storage.sub(0, START);
            drStart = BitHelper.getInt64From(headerBytes, 0);
            drEnd = BitHelper.getInt64From(headerBytes, 8);
            long attemptedWriteStart = BitHelper.getInt64From(headerBytes, 16);
            long attemptedWriteEnd = BitHelper.getInt64From(headerBytes, 24);
            long newDrStart = Math.max(START, Math.min(storage.contentSize(), drStart));
            long newDrEnd = Math.max(START, Math.min(storage.contentSize(), drEnd));

            if(attemptedWriteStart != -1) {//if we crashed while writing the element
                //what we want here: we want to invalidate what MAY have been written(e.g. [attemptedWriteStart, attemptedWriteEnd])
                //the following ranges HAVE to be in the new dirty region:
                //  [attemptedWriteStart, attemptedWriteEnd]
                //  [drStart, drEnd]

                if(newDrStart <= attemptedWriteStart && attemptedWriteEnd <= newDrEnd)
                    return;//write range already marked as dirty

                if(
                    (newDrStart == newDrEnd && newDrEnd == storage.contentSize()) // if last write at end AND appended we need to truncate to dirty region start (everything after is invalid - we don't know whether it was written)
                    ||
                    (attemptedWriteStart == START && attemptedWriteEnd >= Math.max(newDrEnd, newDrStart))//if we wrapped and we wrote over the previous dirty region(attemptedWriteEnd >= newDrEnd), we have to invalidate all of that (if drStart>drEnd, dirty region is [START, drEnd])
                ) {
                    drStart = attemptedWriteStart;
                    truncateToDirtyRegionStart();
                    newDrStart = drStart;
                    newDrEnd = drEnd;
                }

                //the truncate writes before are safe, because they are repeatable and yield the same result
                //after the following commit we are safe anyways
                commit(newDrStart, newDrEnd);
            }
        }
    }


    protected void truncateToDirtyRegionStart() {
        if(drStart < storage.contentSize()) {
            storage.delete(drStart, storage.contentSize());
            drEnd = Math.min(drStart, drEnd);
        }
    }
    protected void preCommit(long newDrEnd, long attemptedWriteStart, long attemptedWriteEnd) {
        //no need to write to instance variables - will write in post
        storage.set(
                0,
                BitHelper.getBytes(drStart), BitHelper.getBytes(newDrEnd),
                BitHelper.getBytes(attemptedWriteStart), BitHelper.getBytes(attemptedWriteEnd)
        );
    }
    protected void writeElem(long at, byte[] li, byte[] e) {
        storage.set(at, li, e);
    }
    protected void commit(long newDrStart, long newDrEnd) {
        drStart = newDrStart;
        drEnd = newDrEnd;
        storage.set(
                0,
                BitHelper.getBytes(newDrStart), BitHelper.getBytes(newDrEnd),
                BitHelper.getBytes(-1L), BitHelper.getBytes(-1L)
        );
    }







    //ADDITIONAL, SECONDARY FUNCTIONALITY - build from above

    /**
     * @return the earliest added element, that is still in this buffer
     */
    public byte[] earliest() {
        return iterator().next_or_null();
    }

    /**
     * Calculates size by iteration, should be considered costly
     * (todo could cache num elements on commit in attemptedWriteStart position - on crash it could be loaded lazily)
     * @return the number of elements currently accessible by the iterator.
     */
    public int size() {
        int counter = 0;
        ExtendedIterator<?> iterator = iterator();
        while(iterator.hasNext()) {
            iterator.skip();
            counter++;
        }
        return counter;
    }

    /**
     * @return whether this buffer is empty: ({@link #size()} == 0
     */
    public boolean isEmpty() {
        return size() == 0;
    }


    public long calculateMaxSingleElementSize() {
        long eUpperBound = max - START;
        int updatedLiSize = LIbae.generateLI(eUpperBound).length;
        int liSize;
        do {
            liSize = updatedLiSize;
            updatedLiSize = LIbae.generateLI(eUpperBound - liSize).length;
        } while (updatedLiSize != liSize);
        return eUpperBound - liSize;
    }


    /**
     * FIFO - enqueues at the end
     * @param bytes to be enqueued
     */
    @Override public void enqueue(byte[] bytes) {
        append(bytes);
    }

    /**
     * FIFO - dequeues at earliest added
     * @return the removed elements
     */
    @Override public byte[] dequeue() {
        byte[] bytes = earliest();//this is efficient enough, the calculations are very different and io is bottleneck
        deleteFirst();
        return bytes;
    }

    /**
     * FIFO - returns earliest added
     * @return earliest
     */
    @Override public byte[] peek() {
        return earliest();
    }
}
