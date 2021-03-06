package jokrey.utilities.transparent_storage.bytes.non_persistent;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Implementation of {@link TransparentBytesStorage} writing the bytes into RAM.
 * Uses a growing and shrinking byte[] to store the data in RAM.
 *
 * Read only access should be thread safe (even though not locking or synchronization is done).
 *    Write access is NOT thread safe for performance reasons.
 *
 * @author jokrey
 */
public class ByteArrayStorage implements TransparentBytesStorage {
    public static boolean attempt_querying_page_size = true;
    private static int assumed_page_size; // //4*1024 i.e. 4KiB - a common page size - should be calculated and not be too big

    static {
        if (attempt_querying_page_size) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                Unsafe unsafe = (Unsafe) f.get(null);
                assumed_page_size = unsafe.pageSize();
            } catch (Throwable t) { //catch literally anything
//                attempt_querying_page_size=false;   // useless. static block only executed once per class loader. different class loader could not see this var.
                assumed_page_size = 4096;
                System.err.println("--- YOUR SYSTEM DOES NOT SEEM TO SUPPORT sun.misc.Unsafe OR UNSAFE PACKAGE CHANGED ---");
                System.err.println("--- CONSIDER SETTING \"ByteArrayStorage.attempt_querying_page_size\" TO \"false\" ---");
                System.err.println("--- ASSUMING PAGE SIZE OF: " + assumed_page_size + " ---");
                System.err.println("--- STACK TRACE FOR DEBUG: ---");
                t.printStackTrace();
            }
        } else {
            assumed_page_size = 4096;
        }
    }

    public byte[] content;
    public int size; //always smaller or equal to content.length

    private final boolean memory_over_performance;

    /**
     * Default, no-arg constructor.
     * Preallocates a number of bytes in internal byte content.
     * The actual number might differ. If you want to be sure, call {@link #ByteArrayStorage(int)}.
     * The number will be what is closest to the page size of the system we are on, for optimal caching.
     * No Java being not particularly close to the hardware, this might not work perfectly and we might not even be able to obtain that page size.
     * But at least we tried.
     */
    public ByteArrayStorage() {
//        this(assumed_page_size);
        this(64);
    }

    @Override public void close() {
        content =null;size=-1;
    }

    /**
     * Preallocates initial_capacity bytes in internal byte content.
     * <p>
     * Used when you already know that you will store a certain amount of bytes later
     *
     * @param initial_capacity initial capacity for the content
     */
    public ByteArrayStorage(int initial_capacity) {
        this(false, new byte[initial_capacity], 0);
    }

    /**
     * Quick init-Constructor
     * Short for: new ByteArrayStorage().setContent(start_buf)
     *
     * @param start_buf initial raw content
     */
    public ByteArrayStorage(byte[] start_buf) {
        this(false, start_buf, start_buf.length);
    }

    /**
     * Initialises all runtime values with given parameter
     *
     * @param memory_over_performance will change the internal grow and shrink behaviour.
     *      For most systems it makes sense to value performance over memory efficiency. i.e. less copy operations.
     *      Sometimes however, for example when only small changes to the initial content size are likely, it makes sense to let the allocated memory be close to the currently needed memory.
     * @param initial_buf initial raw content
     * @param initial_size number of bytes in the initial_buf that are prefilled with meaningful data. If the initial_buf is only used for preallocation set it to 0.
     */
    public ByteArrayStorage(boolean memory_over_performance, byte[] initial_buf, int initial_size) {
        this.memory_over_performance = memory_over_performance;
        setContent(initial_buf);
        this.size = initial_size;
        if(initial_size > content.length)
            throw new IndexOutOfBoundsException("initial_size("+initial_size+") > content.length("+content.length+")");
    }


    @Override public void clear() {
        content = new byte[64];
        size = 0;
    }


    @Override public void setContent(byte[] content) {
        this.content = content;
        size = this.content.length;
    }


    @Override public byte[] getContent() {
        return Arrays.copyOf(content, size);
    }


    @Override public ByteArrayStorage delete(long start, long end) throws StorageSystemException {
        int len = (int) (end - start);
        if (len > 0) {
            if(start>=size || end > size || start < 0 || end < 0) {
                throw new IndexOutOfBoundsException("size("+size+"), start("+start+"), end("+end+")");
            }

            System.arraycopy(content, (int) start + len, content, (int) start, (int) (this.size - end));  //override delete section with rest of array
            size -= len;

            shrink_if_required();

            return this;
        } else {
            throw new StorageSystemException("Cannot delete 0 or less than 0 bytes(delAttemptRange=["+start+", "+end+"]).");
        }
    }


    @Override public ByteArrayStorage insert(long start_long, byte[] val) {
        if(start_long > contentSize()) throw new IndexOutOfBoundsException();
        if (start_long + val.length > Integer.MAX_VALUE)
            throw new StorageSystemException("ByteArrayStorage cannot store this many bytes");
        int start = (int) start_long;
        int end = start + val.length;
        int requiredSize = start + val.length;
        grow_to_at_least(requiredSize);

        int size_before = size;
        size = size_before + val.length;

        System.arraycopy(content, start, content, end, size_before - start); //copy data start of insert area to end insert area
        System.arraycopy(val, 0, content, start, end - start); //copy data in insert area to end of insert area

        return this;
    }

    @Override public ByteArrayStorage set(long start_long, InputStream content, long content_length_long) throws StorageSystemException {
        if(start_long > contentSize()) throw new IndexOutOfBoundsException();
        if (start_long + content_length_long > Integer.MAX_VALUE)
            throw new StorageSystemException("ByteArrayStorage cannot store InputStream of length: " + content_length_long);
        int start = (int) start_long;
        int promised_content_length = (int) content_length_long;

        try {
            grow_to_at_least(start + promised_content_length);
            size = (int) Math.max(start+promised_content_length, contentSize());//even if stream fails libae will still be able to continue..
                                                                                // Because bytes not filled with meaningful data are at least allocated and calculated as if they were normally written

            int nRead;
            int total_read = 0;
            byte[] is_buffer = new byte[assumed_page_size];
            while (total_read < promised_content_length && (nRead = content.read(is_buffer, 0, Math.min(is_buffer.length, promised_content_length-total_read))) != -1) {
                System.arraycopy(is_buffer, 0, this.content,  start+total_read, nRead);

//                System.err.println("nRead: "+nRead);

                total_read += nRead;
            }
            content.close();

            System.err.println("total_read = " + total_read);
            System.err.println("promised_content_length = " + promised_content_length);

            if (total_read < promised_content_length) {
                throw new StorageSystemException("The provided stream failed to deliver the promised number of bytes - total_read="+total_read+", promised="+promised_content_length);
            }
        } catch (IOException ex) {
            throw new StorageSystemException("IO Exception thrown by provided InputStream(" + ex.getMessage() + ").");
        }
        return this;
    }


    @Override public byte[] sub(long start, long end) throws StorageSystemException {
        if (end > size) end = size; //to satisfy interface doc condition
        if (start < 0) start = 0;
        int len = (int) (end - start);
        if (len > 0) {
            byte[] sub = new byte[len];
            System.arraycopy(content, (int) start, sub, 0, len);
            return sub;
        } else if (len == 0)
            return new byte[0];
        else
            throw new StorageSystemException("Cannot create a byte array with less than 0 bytes. - sub: start="+start+", end="+end);
    }

    @Override public ByteArrayStorage set(long start, byte[] part, int off, int len) throws StorageSystemException {
//        if(start > size) throw new StorageSystemException("Too large, start("+start+") > size("+size+")"); //would limit appending...
//        System.out.println("set - start = " + start + ", part.length = " + part.length + ", off = " + off + ", len = " + len);
        long requiredSizeOfAttachedPart = start+len;
        grow_to_at_least(requiredSizeOfAttachedPart);
        System.arraycopy(part, off, content, (int) start, Math.min(part.length, len));
        if(requiredSizeOfAttachedPart > size)
            size = (int) requiredSizeOfAttachedPart;
        return this;
    }

    @Override public ByteArrayStorage set(long start, byte part) throws StorageSystemException {
        long requiredSizeOfAttachedPart = start+1;
        grow_to_at_least(requiredSizeOfAttachedPart);
        content[(int) start] = part;
        if(requiredSizeOfAttachedPart > size) size = (int) requiredSizeOfAttachedPart;
        return this;
    }

    @Override public TransparentBytesStorage set(long at, byte[]... parts) throws StorageSystemException {
        for(byte[] part : parts) {
            set(at, part);
            at += part.length;
        }
        return this;
    }

    @Override public InputStream substream(long start, long end) throws StorageSystemException {
        return new ByteArrayInputStream(sub(start, end)); //todo - custom stream, this is rather overkill and problematic for end >>> start
    }

    @Override public InputStream stream() {
        return new ByteArrayInputStream(content);
    }

    @Override public ByteArrayStorage copyInto(long start, byte[] b, int off, int len) {
        System.arraycopy(content, (int) start, b, off, len);
        return this;
    }

    @Override public byte getByte(long index) {
        return content[(int) index];
    }

    @Override public long contentSize() {
        return size;
    }

    @Override public boolean isEmpty() {
        return size == 0;
    }

    @Override public int hashCode() {
        int result = Integer.hashCode(size);
        for (byte element : content)
            result = 31 * result + element;
        return result;
    }
    @Override public boolean equals(Object o) {
        if(! (o instanceof ByteArrayStorage)) return false;
        ByteArrayStorage that = ((ByteArrayStorage) o);
        if(size != that.size) return false;
        for(int i=0;i<size;i++)
            if(content[i] != that.content[i])
                return false;
        return true;
    }


    @Override public synchronized String toString() {
        return "[ByteArrayStorage: l=" + size + ", cont=" + Arrays.toString(content) + "]";
    }


    private void shrink_if_required() {
        if(memory_over_performance) {
            resize_to_next_bigger_page_size(size);
        } else {
            //arbitrary/heuristic numbers
            final int min_amount_of_memory_saved = 1000;

            if(size < content.length / 2 &&
                    // and distance greater than a "min_amount_of_memory_saved" pages (so that we actually win something reasonable here).
                    (content.length - size) > min_amount_of_memory_saved*assumed_page_size
            ) {
                resize_to_next_bigger_page_size(size);
            }
        }
    }

    public void grow_to_at_least(long at_least) {
        content = grow_to_at_least(content, size, at_least, memory_over_performance);
    }
    public static byte[] grow_to_at_least(byte[] orig, int orig_len, long at_least, boolean memory_over_performance) {
        if(at_least <= orig.length) return orig;
        if(at_least + orig.length > Integer.MAX_VALUE) //overflow right away
            throw new OutOfMemoryError();

        if(memory_over_performance) {//yeah, but lets still not do precise allocation. That'd be insane
            if(orig.length < assumed_page_size && at_least < assumed_page_size) {
                int next_power_of_two = getNextPowerOfTwo(at_least);
                if(next_power_of_two > assumed_page_size) //unlikely, but still nice to have...
                    return resize_to_next_bigger_page_size(orig, orig_len, at_least);
                else
                    return resize_to(orig, orig_len, next_power_of_two);
            } else
                return resize_to_next_bigger_page_size(orig, orig_len, at_least);
        } else {
            //arbitrary/heuristic numbers
            final int min_allocated_memory = 512;
            final int max_amount_of_pages_over_allocated = 10000;

            long bytes_currently_under_allocated = at_least - orig.length;
            //min amount of memory allocated - unlikely that smaller than page size, but still nice to have...
            long new_buffer_length_initial_proposal = Math.max(at_least + bytes_currently_under_allocated, //overallocate at least twice what we would have needed
                                                            Math.max(min_allocated_memory, orig.length*2L));
            long new_buffer_length = get_next_bigger_page_size(new_buffer_length_initial_proposal);
            if(new_buffer_length > Integer.MAX_VALUE) {
                new_buffer_length = get_next_bigger_page_size(at_least);
            }

            //try to conciously not over allocate too much
            //This is the biggest difference to the legacy version. It is more memory conscious, but allocates less. so on big arrays it might not perform quite as good
            long bytes_to_be_over_allocated = new_buffer_length - at_least;
            //given: new_buffer_length >= at_least
            if(    (bytes_to_be_over_allocated > max_amount_of_pages_over_allocated*assumed_page_size  && // make sure not to over-allocate too many pages
                    bytes_to_be_over_allocated > bytes_currently_under_allocated) ) {                            // but also allow it if that over-allocation is less than the required allocation now
                new_buffer_length = get_next_bigger_page_size(at_least + max_amount_of_pages_over_allocated * assumed_page_size);
            }

            if(new_buffer_length > Integer.MAX_VALUE)
                throw new OutOfMemoryError();

//            if(content.length!=new_buffer_length) {
//                System.out.println("Resized - at_least: " + at_least);
//                System.out.println("Resized - content.length: " + content.length);
//                System.out.println("Resized - new_buffer_length: " + new_buffer_length);
//                System.out.println("Resized - bytes previously under-allocated: " + (at_least - content.length));
//                System.out.println("Resized - bytes over allocated: " + (new_buffer_length - at_least));
//                System.out.println();
//            }
            return resize_to(orig, orig_len, (int) new_buffer_length);
        }
    }

    private static int getNextPowerOfTwo(long from) {
        return (int) Math.pow(2, Math.ceil(Math.log(from)/Math.log(2)));
    }

    private void resize_to_next_bigger_page_size(long from) {
        content = resize_to_next_bigger_page_size(content, size, from);
    }
    public static byte[] resize_to_next_bigger_page_size(byte[] orig, int orig_len, long from) {
        // Thanks to the magic of integer division this is NOT the same as: "at_least + pageSize"  (which would be wrong)
        long new_size = get_next_bigger_page_size(from);
        if(new_size > Integer.MAX_VALUE)
            throw new OutOfMemoryError();
        else
            return resize_to(orig, orig_len, (int) get_next_bigger_page_size(from));
    }
    private static long get_next_bigger_page_size(long from) {
        return assumed_page_size*((from/assumed_page_size) + 1);
    }
    public static byte[] resize_to(byte[] orig, int orig_len, int new_raw_buffer_length) {
        if(new_raw_buffer_length != orig.length) { // avoid useless copy operation
            byte[] temp = new byte[new_raw_buffer_length];
            System.arraycopy(orig, 0, temp, 0, orig_len);
            return temp;
        }
        return orig;
    }







    //PREVIOUS GROW SHRINK BEHAVIOUR - PROVED TO BE TOO SLOW

//    //dynamic growing and shrinking - when content manipulated here the result content.length will always be > size and always be divisible by assumed_page_size
//    private void shrink_if_required() {
////        //the heuristics here work with the assumption that when having a large array, that changes will also be large
////        //      and that a small overhead in allocated memory is preferable to the overhead of shrinking and growing with each operation
////        final double size_difference_trigger_in_percent = 0.15;  //technically an arbitrary number
////        final int total_difference_trigger_in_page_count = 100;  //technically an arbitrary number
////        if(size < content.length && (
////                // when size is half as big as the content and they are bigger than the page size
////                (size < content.length/2 && content.length>assumed_page_size)
////                        ||
////                        // when size difference is greater than x percent of total page count in memory
////                        (((content.length-size) / assumed_page_size) >= (int) ((content.length / assumed_page_size) * size_difference_trigger_in_percent))
////                        ||
////                        //when size difference greater than x pages
////                        (((content.length-size) / assumed_page_size) >= total_difference_trigger_in_page_count)
////        )) {
////            resize_to_next_bigger_page_size(size);
////        }
//
//        //when the heuristics above prove not to be beneficial, then just use this:
//        if(size < content.length) {
//            resize_to_next_bigger_page_size(size);
//        }
//    }
//    private void grow_to_at_least(int at_least) {
//        //not using as heuristics like shrink, because it seems wrong to pre allocate large amounts of possibly never used memory
//        if(at_least > content.length) {
//            resize_to_next_bigger_page_size(at_least);
//        }
//    }

    public static byte[] getConcatenated(byte[]... parts) {
        int partsLength = 0;
        for(byte[] part:parts) partsLength += part.length;

        byte[] concatenated = new byte[partsLength];
        int counter = 0;
        for(byte[] part:parts) {
            System.arraycopy(part, 0, concatenated, counter, part.length);
            counter += part.length;
        }
        return concatenated;
    }
}
