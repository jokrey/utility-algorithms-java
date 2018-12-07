package jokrey.utilities.encoder.tag_based.implementation.length_indicator;

import jokrey.utilities.simple.data_structure.ExtendedIterator;
import jokrey.utilities.encoder.tag_based.Encodable;
import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.TransparentStorage;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static jokrey.utilities.simple.data_structure.ExtendedIterator.createArrayFrom;
import static jokrey.utilities.simple.data_structure.ExtendedIterator.getTypeClassFor;

/**
 * Non thread safe, but non altering methods are reentrant and thread safe.
 *     As long as the underlying {@link TransparentStorage} system is reentrant and thread safe in it's non altering methods.
 *
 * This is the superclass for any length indicator based encoder.
 *     Technically this will work for any SF(StorageFormat) that can contain many of itself (any array) and has a length property (any array)
 *     One just has to find a function that returns a length indicator for the SF and a f^-1 function to turns that into boundaries again.
 * Idea: This is essentially just a weird type of iterator that can directly work on serialized data.
 *       It is used by {@link LITagEncoder} to store tag-value pairs.
 *
 * Implementations exist for Strings (utf8) and byte strings (byte[])
 * @author jokrey
 */
public abstract class LIe<SF> implements Encodable<SF>, Iterable<SF> {
    private final TransparentStorage<SF> storage;

    /**
     * Initialises internal storage with storage
     *
     * This constructor should be hidden from enc users
     * @param storage internal storage
     */
    protected LIe(TransparentStorage<SF> storage) {
        this.storage=storage;
    }

    /**
     * Initialises internal storage with storage and sets internal storage content to encoded
     * Shortcut for: new LIse().readFromEncoded(encoded);
     * @param encoded initial raw content
     */
    public LIe(TransparentStorage<SF> storage, SF encoded) {
        this(storage);
        readFromEncoded(encoded);
    }

    /**
     * Initialises internal storage with storage and encodes all initial values directly into the storage system.
     * Same as: new LIe(storage).li_encode(initial_values);
     * @param initial_values values to be encoded
     */
    public LIe(TransparentStorage<SF> storage, SF... initial_values) {
        this(storage);
        li_encode(initial_values);
    }


    //LI - OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE

    /**
     * Only used internally
     */
    protected abstract SF getLengthIndicatorFor(SF val);

    /**
     * CANNOT not change anything within current, but this is so much quicker than calling getContent on it before
     * Only used internally (protected)
     *
     * @param start_pos start searching at
     * @param current curring working state
     * @return next start and end index of the li encoded SF. Calling current.sub(start, end) should yield the decoded SF.
     */
    protected abstract long[] get_next_li_bounds(Position start_pos, TransparentStorage<SF> current);

    //LI - OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE OVERRIDE


    @Override public SF getEncoded() {
        return storage.getContent();
    }

    /** @return all previously encoded values */
    public SF[] li_decode_all() {
        return li_decode(reset(), -1);
    }
    /** @return first previously encoded value or null if there is none */
    public SF li_decode_first() {
        return li_decode(reset());
    }

    /**
     * Sets pos to "next" position.
     * @param pos position to read from.
     *            Has to be valid (ensure that no invalidating operation was executed on the internal storage with another position value)
     *            Position will be internally altered to point to the "next" value
     * @return number of bytes in the value skipped.
     */
    public long li_skip(Position pos) {
        long[] startIndex_endIndex_ofNextLI = get_next_li_bounds(pos, storage);
        if(startIndex_endIndex_ofNextLI != null) {
            pos.pointer = startIndex_endIndex_ofNextLI[1];
            return startIndex_endIndex_ofNextLI[1] - startIndex_endIndex_ofNextLI[0];
        }
        return -1;
    }

    /**
     * Decodes the next value and sets the position to the next position
     * @param pos position to read from.
     *            Has to be valid (ensure that no invalidating operation was executed on the internal storage with another position value)
     *            Position will be internally altered to point to the "next" value
     * @return the next value
     */
    public SF li_decode(Position pos) {
        return li_decode_single(pos, false);
    }

    /** @see #li_decode(Position) */
    public SF[] li_decode(Position pos, int limit) {
        return li_decode_multiple(pos, limit, false);
    }

    /** @return a valid default start position */
    public Position reset() {
        return new Position();
    }


    //"SAFE" WRITE OPERATIONS::

    /** encodes and appends val to the internal storage */
    public LIe<SF> li_encode(SF val) {
        storage.append(getLengthIndicatorFor(val)).append(val);
        return this;
    }

    /** encodes and appends vals in order to the internal storage */
    public LIe<SF> li_encode(SF... vals) {
        for(SF val:vals)
            li_encode(val);
        return this;
    }


    //INVALIDATING WRITE OPERATIONS:::::

    /**
     * MIGHT INVALIDATE PREVIOUS POSITIONS
     * {@inheritDoc}
     */
    @Override public LIe<SF> readFromEncoded(SF encoded) {
        storage.setContent(encoded);
        return this;
    }

    /** Clears the internal storage system.
     * MIGHT INVALIDATE PREVIOUS POSITIONS
     * @see TransparentStorage#clear() */
    public LIe<SF> clear() {
        storage.clear();
        return this;
    }

    /**
     * MIGHT INVALIDATE PREVIOUS POSITIONS
     * Deletes next encoded value at position and resets the position so that it will decode the "next" value next time.
     * @param pos position to read from.
     *            Has to be valid (ensure that no invalidating operation was executed on the internal storage with another position value)
     *            Position will be internally altered to point to the "next" value
     * @return decoded value or null when no more values are available or something invalid when pos was invalid.
     */
    public SF li_decode_and_delete(Position pos) {
        return li_decode_single(pos, true);
    }

    /** @see #li_decode_and_delete(Position) */
    public SF[] li_decode_and_delete(Position pos, int limit) {
        return li_decode_multiple(pos, limit, true);
    }

    /** Like decode_and_delete, but doesn't decode and saves that cost.
     * @see #li_decode_and_delete(Position) */
    public long li_delete(Position pos) throws StorageSystemException {
        long[] startIndex_endIndex_ofNextLI = get_next_li_bounds(pos, storage);
        if(startIndex_endIndex_ofNextLI != null) {
            storage.delete(pos.pointer, startIndex_endIndex_ofNextLI[1]);
            //do not set read pointer, because it will now automatically point to the next item
            return startIndex_endIndex_ofNextLI[1] - pos.pointer;
        } else
            return -1;
    }

    /** Like decode_and_delete, but doesn't decode and saves that cost.
     * @see #li_decode_and_delete(Position) */
    public long li_delete(Position pos, int limit) throws StorageSystemException {
        long deleted_bytes_counter = 0;
        for(int i=0;i<limit;i++)
            deleted_bytes_counter += li_delete(pos);
        return deleted_bytes_counter;
    }








    /**{@inheritDoc}
     * Supports remove
     * Encoding new values concurrently will work
     * Deleting values might, but likely won't work.
     */
    @Override public ExtendedIterator<SF> iterator() {
        return iterator(reset());
    }
    /** Iterates from provided position
     * @see #iterator() */
    public ExtendedIterator<SF> iterator(Position iterator) {
        return new ExtendedIterator<SF>() {
            @Override public boolean hasNext () {
                try {
                    return iterator.pointer < storage.contentSize();
                } catch (StorageSystemException e) {
                    return false;
                }
            }

            @Override public SF next() {
                SF decoded = next_or_null();
                if(decoded!=null)
                    return decoded;
                else
                    throw new NoSuchElementException("No more elements available. (Concurrent delete access?)");
            }

            @Override public SF next_or_null() {
                try {
                    return li_decode(iterator);
                } catch (StorageSystemException e) {
                    throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+").");
                }
            }

            @Override public void skip() {
                try {
                    long decoded = li_skip(iterator);
                    if(decoded<0)
                        throw new NoSuchElementException("No more elements available. (Concurrent delete access?)");
                } catch (StorageSystemException e) {
                    throw new NoSuchElementException("Internal Storage System Exception of sorts("+e.getMessage()+").");
                }
            }

            @Override public void remove() {
                li_delete(iterator);
                //setting the pointer is not necessary. It now automatically points to the next value.

//                long number_of_deleted_bytes = li_delete(iterator);
//                //special feature to at least keep the internal iterator pointer
//                if(this_iterator.pointer== iterator.pointer) {
//                    //We have a problem. The value the non-iterator libae has is now invalid(deleted)...
//                    //this is a huge issue, but we'll simply address it by pointing the pointer to a sensible default value
//                    this_iterator.pointer = 0;
//                } else if(this_iterator.pointer> iterator.pointer) {
//                    //We have a problem. The old read pointer will point to an invalid index or at least an unexpected index
//                    //therefore we'll have to change the pointer after
//                    this_iterator.pointer -= number_of_deleted_bytes;
//                    //one might think that old_read_pointer can now be negative.
//                    // But that is not possible, unless the underlying storage system was altered.
//                    // The read pointer always points to the first of a tuple, we are only deleting a tuple here
//                }
            }
        };
    }

    /**
     * Accessor for storage system
     * Changing data in that system might invalidate storage positions and might make raw data inaccessible.
     * @return storage system
     */
    public TransparentStorage<SF> getStorageSystem() {
        return storage;
    }

    /**
     * Shortcut to obtain storage size when written (size format depends on storage format)
     * @return size
     */
    public long storageSize() {return storage.contentSize();}


    //standard::
    @Override abstract public String toString();
    @Override public int hashCode() {
        return storage.hashCode();
    }
    @Override public boolean equals(Object o) {
        return o instanceof LIe && storage.equals(((LIe)o).storage);
    }

    /**
     * Safer to use Pointer class. Can only be instantiated from inside this system,
     *  and is therefore guaranteed to contain a valid position(at least as long as no write operation is executed before).
     * Basically just an immutable long, when seen from outside this class.
     * Inside this class the pointer is mutable and can therefore only be set to valid positions.
     */
    public static class Position {
        private long pointer;
        protected Position() {
            this(0);
        }
        protected Position(long pointer) {
            this.pointer = pointer;
        }
        public Position copy() {
            return new Position(pointer);
        }

        @Override public boolean equals(Object obj) {
            return obj instanceof Position && pointer == ((Position)obj).pointer;
        }
        @Override public int hashCode() {
            return Long.hashCode(pointer);
        }
    }
    protected long get(Position p) {return p.pointer;}
    protected void set(Position p, long unsafe_raw_value) {p.pointer = unsafe_raw_value;}
    protected Position from(long unsafe_raw_value) {return new Position(unsafe_raw_value);}

    /**
     * Checks whether or not the iterator position might still produce a valid decoding when attempted.
     * Since the check does not actually attempt that decoding, it is NOT guaranteed that a decode will return a valid value after this check.
     *
     * Operation will query the size of the storage system.
     * If that operation is slow, then this method may be slow.
     * @param p a position.
     * @return whether or not the position is assumed to be valid at first glance
     */
    public boolean still_valid(Position p) {
        return p.pointer >= 0 && p.pointer < storageSize();
    }


    //FUNCTIONALITY::

    private SF li_decode_single(Position pos, boolean delete_decoded) {
        long[] startIndex_endIndex_ofNextLI = get_next_li_bounds(pos, storage);
        if(startIndex_endIndex_ofNextLI != null) {
            SF decoded = storage.sub(startIndex_endIndex_ofNextLI[0], startIndex_endIndex_ofNextLI[1]);
            if(delete_decoded) {
                storage.delete(pos.pointer, startIndex_endIndex_ofNextLI[1]);
            } else {
                pos.pointer = startIndex_endIndex_ofNextLI[1];
            }
            return decoded;
        } else {
            return null;
        }
    }

    private SF[] li_decode_multiple(Position pos, int limit, boolean delete_decoded) {
        ArrayList<SF> list = new ArrayList<>();
        SF dec_single = null;
        while((limit<0 || list.size()<limit) && (dec_single = li_decode_single(pos, delete_decoded)) != null)
            list.add(dec_single);

        if(dec_single==null)
            return createArrayFrom(list, getTypeClassFor(getClass()));
        else
           return createArrayFrom(list, dec_single);
    }
}
