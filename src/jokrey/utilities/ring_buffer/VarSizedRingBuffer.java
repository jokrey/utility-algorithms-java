package jokrey.utilities.ring_buffer;

import jokrey.utilities.bitsandbytes.BitHelper;
import jokrey.utilities.encoder.as_union.li.LIPosition;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.simple.data_structure.ExtendedIterator;
import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VarSizedRingBuffer {
    public final long max;

    protected final TransparentBytesStorage storage;

    protected final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public VarSizedRingBuffer(TransparentBytesStorage storage, long max) {
        if(max < START) throw new IllegalArgumentException("max cannot be smaller than header("+START+" bytes)");
        if(max < storage.contentSize()) throw new IllegalArgumentException("max cannot be smaller than current storage size - truncate first");
        this.max = max;
        this.storage = storage;

        initHeader();
    }

    public void write(byte[] e) {
        rwLock.writeLock().lock();
        try {
            byte[] li = LIbae.generateLI(e.length);
            int lieSize = li.length + e.length;
//            System.out.println("write(e) before - lwl(" + lwl+"), cole("+cole+"), lieSize("+lieSize+")");

            //calc new-lwl
            long newLwl = lwl + lieSize;
            if (newLwl > max) {
                newLwl = START + lieSize;
                if(newLwl > max)
                    throw new IllegalArgumentException("element to large");
                if (cole > lwl)//only if not recovering
                    truncateToCOLE();
            }

            //calc new-cole
            long newCole;
            if (cole == storage.contentSize()) {
                if (newLwl >= storage.contentSize()) {
                    newCole = newLwl;
                } else {
                    long[] liBounds = readLIBoundsAt(START);
                    if(liBounds == null) throw new IllegalStateException("could not read li at start (corrupt data)");
                    newCole = liBounds[1];
                }
            } else {
                newCole = cole;
            }
            while (newLwl > newCole) {
                if(newCole >= storage.contentSize()) {
                    newCole = newLwl;
                } else {
                    long[] liBounds = readLIBoundsAt(newCole);
                    //the following line should never be called, unless corrupt archive - because newCole just jumps and writeAt is always in bounds
                    if (liBounds == null) {
                        System.out.println("liBounds == null ");
//                        System.out.println("cole = " + cole);
//                        System.out.println("lwl = " + lwl);
//                        System.out.println("newCole = " + newCole);
//                        System.out.println("newLwl = " + newLwl);
//                        System.out.println("lieSize = " + lieSize);
//                        System.out.println("contentSize = " + storage.contentSize());
//                        throw new IllegalStateException("could not read li at newCole(" + newCole + ") (corrupt data)");
                        //occurs
                        newCole = storage.contentSize();
                    } else {
                        newCole = liBounds[1];
                    }
                }
            }

            long writeAt = newLwl - lieSize;
            //write order hella important for free and automatic crash recovery
            writePre(newCole, newLwl, writeAt);
            writeElem(writeAt, li, e); //(newLwl - lieSize) often(*) == lwl
            writePost(newCole, newLwl);
//            System.out.println("write(e) after - lwl(" + lwl+"), cole("+cole+"), lieSize("+lieSize+"), at("+(newLwl - lieSize)+")");
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public ExtendedIterator<byte[]> iterator() {
        rwLock.readLock().lock();
        try {
            LIbae decoder = new LIbae(storage);
            AtomicBoolean wrapReadAllowed;
            LIPosition iter;
            if (cole == storage.contentSize()) {
                //on crash between truncate and writeCole, or if never wrapped write, or if dirtyRegion empty and contentSize() == max
                wrapReadAllowed = new AtomicBoolean(false);
                iter = new LIPosition(START);
            } else {
                wrapReadAllowed = new AtomicBoolean(true);
                iter = new LIPosition(cole);
            }
            return new ExtendedIterator<byte[]>() {
                @Override public boolean hasNext () {
                    rwLock.readLock().lock();
                    try {
                        if(!wrapReadAllowed.get() && iter.pointer == lwl) return false;
                        if(iter.hasNext(storage)) return true;
                        if(!wrapReadAllowed.get()) return false;
                        return new LIPosition(START).hasNext(storage);
                    } catch (StorageSystemException e) {
                        throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+"). Kinda indicates no such element");
                    } finally {
                        rwLock.readLock().unlock();
                    }
                }

                @Override public byte[] next_or_null() {
                    rwLock.readLock().lock();
                    try {
                        if (!wrapReadAllowed.get() && iter.pointer == lwl) return null;

                        byte[] next = decoder.decode(iter);
                        if(next != null) return next;

                        if(!wrapReadAllowed.get()) return null;

                        wrapReadAllowed.set(false);
                        iter.pointer = START;
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
                        if (iter.pointer == lwl)
                            throw new NoSuchElementException("Cannot skip, reached eof");
                        long decoded = decoder.skipEntry(iter);
                        if(decoded < 0) {//no next element was available
                            if(!wrapReadAllowed.get())
                                throw new NoSuchElementException("Cannot skip, reached eof");
                            wrapReadAllowed.set(false);
                            iter.pointer = START;
                            skip();
                        }
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






    //LI STUFF:

    public long[] readLIBoundsAt(long pointer) {
        long content_size = storage.contentSize();
        if(pointer+1>content_size)
            return null;
        byte[] cache = storage.sub(pointer, pointer+9); //cache maximum number of required bytes. (to minimize possibly slow sub calls)
//        System.out.println("readLIBoundsAt("+pointer+") - cache = " + Arrays.toString(cache));
        return LIbae.get_next_li_bounds(cache, 0, pointer, content_size);
    }

    protected void writeElem(long at, byte[] li, byte[] e) {
        storage.set(at, li, e);
    }




    //HEADER STUFF:

    public static final int START = 32;

    //last write location
    protected long lwl = -1;
    //current overwritten libae end
    protected long cole = -1;

    protected void initHeader() {
        if (storage.contentSize() <= START) {
            writePost(START, START);
        } else {
            byte[] headerBytes = storage.sub(0, START);
            lwl = BitHelper.getInt64From(headerBytes, 0);
            cole = BitHelper.getInt64From(headerBytes, 8);
            long newerLwl = BitHelper.getInt64From(headerBytes, 16);
            long newerCole = Math.min(cole, storage.contentSize());
            long writeAt = BitHelper.getInt64From(headerBytes, 24);
//            System.out.println("initHeader");
//            System.out.println("lwl = " + lwl);
//            System.out.println("cole = " + cole);
//            System.out.println("newerLwl = " + newerLwl);
//            System.out.println("newerCole = " + newerCole);
//            System.out.println("writeAt = " + writeAt);
            if(newerLwl != lwl) {
                //ok, we now know it crashed between writePre and writePost - the only thing we don't know is whether we already wrote the element or not
                long[] liBoundsAtOld = readLIBoundsAt(writeAt);
//                System.out.println("liBoundsAtOld = " + Arrays.toString(liBoundsAtOld));
                if(liBoundsAtOld==null) {
                    //we have not written element yet, because li bounds at written position are invalid
                    //why writeAt?!
                    writePost(newerCole, writeAt);
                } else if(liBoundsAtOld[1] != newerLwl) {
                    //we know that at element has not been written, because the li bounds are incorrect and do not fit expected end
                    writePost(newerCole, lwl);
                } else if(writeAt == lwl) {
                    //in this case, we know that we have not written element yet, but we need to invalidate the position
                    //     because the positions is coincidentally valid.
                    //   otherwise we would read former first element here (wrong order)
                    //   do not have enough data to recover that first element - and its not worth it...
                    writePost(newerCole, lwl);
                } else {
                    writePost(newerCole, newerLwl);
                }
            } else if(cole != newerCole) {
                writePost(newerCole, lwl);
            }
        }
    }


    protected void truncateToCOLE() {
        if(cole < storage.contentSize())
            storage.delete(cole, storage.contentSize());
    }
    protected void writePre(long newCole, long newLwl, long writeAt) {
        //no need to write to instance variables - will write in post
        storage.set(
                0,
                BitHelper.getBytes(lwl), BitHelper.getBytes(newCole),
                BitHelper.getBytes(newLwl), BitHelper.getBytes(writeAt)
        );
    }
    protected void writePost(long newCole, long newLwl) {
        cole = newCole;
        lwl = newLwl;
        storage.set(
                0,
                BitHelper.getBytes(newLwl), BitHelper.getBytes(newCole),
                BitHelper.getBytes(newLwl), BitHelper.getBytes(-1)
        );
    }
}
