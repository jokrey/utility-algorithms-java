package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.bitsandbytes.BitHelper;
import jokrey.utilities.ring_buffer.VarSizedRingBufferQueueOnly;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static jokrey.utilities.ring_buffer.VarSizedRingBufferQueueOnly.START;

public class VSBRDebugPrint {
    public static void printContents(String ident, VarSizedRingBufferQueueOnly vsrb, TransparentBytesStorage underlyingStorageOfVSBR,
                                     Function<byte[], String> elemTransformer) {
        byte[] headerBytes = underlyingStorageOfVSBR.sub(0, START);
        long drStart = BitHelper.getInt64From(headerBytes, 0);
        long drEnd = BitHelper.getInt64From(headerBytes, 8);
        long attemptedWriteStart = BitHelper.getInt64From(headerBytes, 16);
        long attemptedWriteEnd = BitHelper.getInt64From(headerBytes, 24);

        System.out.println("\n"+ident+" - PRINTING RING BUFFER(max="+vsrb.max+")");
        System.out.println("drStart = " + drStart);
        System.out.println("drEnd = " + drEnd);
        System.out.println("attemptedWriteStart = " + attemptedWriteStart);
        System.out.println("attemptedWriteEnd = " + attemptedWriteEnd);
        System.out.println("storage.contentSize() = " + underlyingStorageOfVSBR.contentSize());
        long dirtyRegionSize = (drEnd - drStart);
        System.out.println("dirty region bounds = ["+drStart+", "+drEnd+"]");
        System.out.println("dirty region size = "+dirtyRegionSize);
        if(drEnd == underlyingStorageOfVSBR.contentSize())
            System.out.println("remaining space without new override = " + (dirtyRegionSize + (vsrb.max - drEnd)));
        else
            System.out.println("remaining space without new override = " + dirtyRegionSize);
        System.out.println("max single element size = " + vsrb.calculateMaxSingleElementSize());
        System.out.print("Raw Memory Layout: ");printMemoryLayout(vsrb, underlyingStorageOfVSBR, elemTransformer, drStart, drEnd, false);
        System.out.print("START converted Memory Layout: ");printMemoryLayout(vsrb, underlyingStorageOfVSBR, elemTransformer, drStart, drEnd, true);
        List<byte[]> rawElements = vsrb.iterator().collect();
        if(elemTransformer == null)
            System.out.println("vsrb elements = " + rawElements);
        else
            System.out.println("vsrb elements(conv) = "+ elementsToList(vsrb, elemTransformer));
    }

    public static List<String> elementsToList(VarSizedRingBufferQueueOnly vsrb, Function<byte[], String> elemTransformer) {
        return vsrb.iterator().collect().stream().map(elemTransformer).collect(Collectors.toList());
    }

    public static void printMemoryLayout(VarSizedRingBufferQueueOnly vsrb, TransparentBytesStorage underlyingStorageOfVSBR, Function<byte[], String> elemTransformer) {
        printMemoryLayout(vsrb, underlyingStorageOfVSBR, elemTransformer, true);
    }
    public static void printMemoryLayout(VarSizedRingBufferQueueOnly vsrb, TransparentBytesStorage underlyingStorageOfVSBR, Function<byte[], String> elemTransformer, boolean convertToStart) {
        byte[] headerBytes = underlyingStorageOfVSBR.sub(0, START);
        long drS = BitHelper.getInt64From(headerBytes, 0);
        long drE = BitHelper.getInt64From(headerBytes, 8);
        printMemoryLayout(vsrb, underlyingStorageOfVSBR, elemTransformer, drS, drE, convertToStart);
    }
    private static void printMemoryLayout(VarSizedRingBufferQueueOnly vsrb, TransparentBytesStorage underlyingStorageOfVSBR, Function<byte[], String> elemTransformer, long drS, long drE, boolean convertToStart) {
        int convSub = convertToStart?START:0;
        System.out.print("memoryLayout("+(underlyingStorageOfVSBR.contentSize() - convSub)+"/"+(vsrb.max- convSub)+"): "+(drS - convSub)+","+(drE - convSub)+"{");
        long p = START;
        long virtualLwl = drS == underlyingStorageOfVSBR.contentSize() && drS>drE ? START : drS;
        while(p < underlyingStorageOfVSBR.contentSize()) {
            if(p == virtualLwl) p = drE;//skip dirty region

            long[] liBounds = vsrb.readForwardLIBoundsAt(p);
            if(liBounds==null)break;
            byte[] e = underlyingStorageOfVSBR.sub(liBounds[0], liBounds[1]);
            System.out.print((elemTransformer == null?"@bytes": elemTransformer.apply(e))+"["+(p - convSub)+", "+(liBounds[0] - convSub)+", "+(liBounds[1] - convSub)+"], ");
            p = liBounds[1];
        }
        System.out.println("}" );
    }
}
