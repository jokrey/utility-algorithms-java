package jokrey.utilities.encoder.as_union.li.bytes;

import jokrey.utilities.bitsandbytes.BitHelper;
import jokrey.utilities.encoder.as_union.li.LIPosition;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.type.transformer.LITypeToBytesTransformer;
import jokrey.utilities.transparent_storage.bytes.non_persistent.ByteArrayStorage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * More efficient mix between LIBae and a type transformer.
 *
 * @author jokrey
 */
public class MessageEncoder extends ByteArrayStorage {
    public int offset;

    public MessageEncoder() { this(64); }
    public MessageEncoder(int initial_capacity) { this(false, new byte[initial_capacity], 0, 0); }
    public MessageEncoder(byte[] start_buf) { this(false, start_buf, 0, start_buf.length); }
    public MessageEncoder(boolean memory_over_performance, byte[] initial_buf, int offset, int initial_size) {
        super(memory_over_performance, initial_buf, initial_size);
        this.offset = offset;
        resetPointer();
    }

    /** sets the payload to the given bytes - reverse of {@link #asBytes()} */
    public void setBytes(byte[] bs) {
        System.arraycopy(bs, 0, content, offset, bs.length);
        size = offset + bs.length;
    }
    /** sets the payload to the given string - reverse of {@link #asString()} */
    public void setString(String s) {
        setBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /** decodes all payload bytes - reverse of {@link #setBytes(byte[])} */
    public byte[] asBytes() {
        return Arrays.copyOfRange(content, offset, size);
    }
    /** decodes all payload bytes as a utf8 string - reverse of {@link #setString(String)} */
    public String asString() {
        return new String(content, offset, size-offset, StandardCharsets.UTF_8);
    }



    private int pointer=-1;
    /** resets the internal iterating pointer */
    public void resetPointer() {pointer = offset;}

    /** encodes a boolean at pointer position - reverse of {@link #nextBool()} */
    public void encode(boolean b) {
        grow_to_at_least(pointer+1);
        content[pointer++] = (byte) (b?1:0);
    }
    /** encodes a byte at pointer position - reverse of {@link #nextByte()} */
    public void encode(byte b) {
        grow_to_at_least(pointer+1);
        content[pointer++] = b;
    }
    /** encodes a short at pointer position - reverse of {@link #nextShort()} */
    public void encode(short s) {
        grow_to_at_least(pointer+2);
        BitHelper.writeInt16(content, pointer, s);
        pointer+=2;
    }
    /** encodes an int at pointer position - reverse of {@link #nextInt()} */
    public void encode(int i) {
        grow_to_at_least(pointer+4);
        BitHelper.writeInt32(content, pointer, i);
        pointer+=4;
    }
    /** encodes a long at pointer position - reverse of {@link #nextLong()} */
    public void encode(long l) {
        grow_to_at_least(pointer+8);
        BitHelper.writeInt64(content, pointer, l);
        pointer+=8;
    }
    /** encodes a float(32) at pointer position - reverse of {@link #nextFloat()} */
    public void encode(float f) {
        grow_to_at_least(pointer+4);
        BitHelper.writeFloat(content, pointer, f);
        pointer+=4;
    }
    /** encodes a double/float64 at pointer position - reverse of {@link #nextDouble()} */
    public void encode(double d) {
        grow_to_at_least(pointer+8);
        BitHelper.writeDouble(content, pointer, d);
        pointer+=8;
    }
    /** encodes a byte array of variable length at pointer position - reverse of {@link #nextVariable()} */
    public void encodeVariable(byte[] bs) {
        int liBytesWritten = LIbae.writeLI(bs.length, this, pointer);
        pointer+=liBytesWritten;
        set(pointer, bs);
        pointer+=bs.length;
    }
    public void encodeVariableString(String s) {
        encodeVariable(s.getBytes(StandardCharsets.UTF_8));
    }

    //DECODE HELPER
    /** decodes the next payload byte as a boolean - reverse of {@link #encode(boolean)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_boolean(byte[])}) */
    public boolean nextBool() {
        return content[pointer++] == 1;
    }
    /** decodes the next payload byte as a byte  - reverse of {@link #encode(byte)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_byte(byte[])}) */
    public byte nextByte() {
        return content[pointer++];
    }
    /** decodes the next 2 payload bytes as an integer(16bit) - reverse of {@link #encode(short)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_int(byte[])}) */
    public short nextShort() {
        int before = pointer;
        pointer+=2; //temp maybe useless if this evaluates to pointer value before...
        return BitHelper.getInt16From(content, before);
    }
    /** decodes the next 4 payload bytes as an integer(32bit) - reverse of {@link #encode(int)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_int(byte[])}) */
    public int nextInt() {
        int before = pointer;
        pointer+=4;
        return BitHelper.getInt32From(content, before);
    }
    /** decodes the next 8 payload bytes as an integer(64bit) - reverse of {@link #encode(long)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_long(byte[])}) */
    public long nextLong() {
        int before = pointer;
        pointer+=8;
        return BitHelper.getIntFromNBytes(content, before, 8);
    }
    /** decodes the next 4 payload bytes as a floating point number(32 bit) - reverse of {@link #encode(float)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_float(byte[])} (byte[])}) */
    public float nextFloat() {
        int before = pointer;
        pointer+=4;
        return BitHelper.getFloat32From(content, before);
    }
    /** decodes the next 8 payload bytes as a floating point number(64 bit) - reverse of {@link #encode(double)}
     * (same as, but more context efficient {@link LITypeToBytesTransformer#detransform_double(byte[])} (byte[])}) */
    public double nextDouble() {
        int before = pointer;
        pointer+=8;
        return BitHelper.getFloat64From(content, before);
    }
    /** decodes the next n payload bytes as bytes - uses length indicator functionality to determine n
     *  reverse of {@link #encodeVariable(byte[])}
     * (same as, but more context efficient {@link LIbae#decode(LIPosition)}) */
    public byte[] nextVariable() {
        long[] li_bounds = LIbae.get_next_li_bounds(content, pointer, pointer, contentSize() - 1);
        if(li_bounds == null) return null;
        pointer = (int) li_bounds[1];
        return Arrays.copyOfRange(content, (int) li_bounds[0], (int) li_bounds[1]);
    }
    /** decodes the next n payload bytes as a utf8 string - uses length indicator functionality to determine n
     *  reverse of {@link #encodeVariableString(String)}
     * (same as, but more context efficient {@link LIbae#decode(LIPosition)}) */
    public String nextVariableString() {
        long[] li_bounds = LIbae.get_next_li_bounds(content, pointer, pointer, contentSize() - 1);
        if(li_bounds == null) return null;
        pointer = (int) li_bounds[1];
        return new String(content, (int) li_bounds[0], (int) (li_bounds[1]-li_bounds[0]), StandardCharsets.UTF_8);
    }


    /** moves all payload bytes (in range(offset, size)), by 'by' bytes and resets the offset = offset+by. */
    public void shiftOffset(int by) {
        grow_to_at_least(offset+size+by);
        System.arraycopy(content, offset, content, offset+by, size-offset);
        offset+=by;
    }

    public static MessageEncoder from(int offset, byte[] bytes) {
        byte[] content = new byte[offset+bytes.length];
        return new MessageEncoder(true, content, offset, content.length);
    }
    public static MessageEncoder encodeAll(int offset, Object... os) {
        MessageEncoder encoder = new MessageEncoder(true, new byte[offset + os.length*8], offset, 0);
        for(Object o : os) {
                 if(o instanceof Boolean) encoder.encode((Boolean) o);
            else if(o instanceof Byte) encoder.encode((Byte) o);
            else if(o instanceof Short) encoder.encode((Short) o);
            else if(o instanceof Integer) encoder.encode((Integer) o);
            else if(o instanceof Long) encoder.encode((Long) o);
            else if(o instanceof Float) encoder.encode((Float) o);
            else if(o instanceof Double) encoder.encode((Double) o);
            else if(o instanceof byte[]) encoder.encodeVariable((byte[]) o);
            else if(o instanceof String) encoder.encodeVariableString((String) o);
            else throw new IllegalArgumentException("unsupported class exception "+o.getClass());
        }
        return encoder;
    }
}
