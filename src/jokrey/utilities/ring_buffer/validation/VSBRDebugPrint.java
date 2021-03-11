package jokrey.utilities.ring_buffer.validation;

import jokrey.utilities.bitsandbytes.BitHelper;
import jokrey.utilities.encoder.as_union.li.bytes.LIbae;
import jokrey.utilities.ring_buffer.VarSizedRingBuffer;
import jokrey.utilities.transparent_storage.bytes.TransparentBytesStorage;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VSBRDebugPrint {
    public static void printContents(VarSizedRingBuffer vsbr, TransparentBytesStorage underlyingStorageOfVSBR,
                                     Function<byte[], String> elemTransformer) {
//        byte[] headerBytes = underlyingStorageOfVSBR.sub(0, VarSizedRingBuffer.START);
//        long lwl = BitHelper.getInt64From(headerBytes, 0);
//        long cole = BitHelper.getInt64From(headerBytes, 8);
//
//        System.out.println("PRINTING RING BUFFER(max="+vsbr.max+")");
//        System.out.println("lwl = " + lwl);
//        System.out.println("cole(raw-read) = " + cole);
//        System.out.println("cole(sanitized as init would) = " + Math.min(cole, underlyingStorageOfVSBR.contentSize()));
//        System.out.println("storage.contentSize() = " + underlyingStorageOfVSBR.contentSize());
//        long dirtyRegionSize = (cole - lwl);
//        System.out.println("dirty region bounds = ["+lwl+", "+cole+"]");
//        System.out.println("dirty region size = "+dirtyRegionSize);
//        if(cole == underlyingStorageOfVSBR.contentSize())
//            System.out.println("remaining space without new override = " + (dirtyRegionSize + (vsbr.max - cole)));
//        else
//            System.out.println("remaining space without new override = " + dirtyRegionSize);
//        System.out.println("max single element size = " + calculateMaxSingleElementSize(vsbr));
//        List<byte[]> rawElements = vsbr.iterator().collect();
//        if(elemTransformer == null)
//            System.out.println("vsbr elements = " + rawElements);
//        else
//            System.out.println("vsbr elements(conv) = "+rawElements.stream().map(elemTransformer).collect(Collectors.toList()));
//
//        System.out.print("memoryLayout = {" );
//        long p = VarSizedRingBuffer.START;
//        while(p < underlyingStorageOfVSBR.contentSize()) {
//            if(p == lwl) p=cole;//skip dirty region
//
//            long[] liBounds = vsbr.readLIBoundsAt(p);
//            if(liBounds==null)break;
//            byte[] e = underlyingStorageOfVSBR.sub(liBounds[0], liBounds[1]);
//            System.out.print((elemTransformer == null?"@bytes":elemTransformer.apply(e))+"["+p+", "+liBounds[0]+", "+liBounds[1]+"], ");
//            p = liBounds[1];
//        }
//        System.out.println("}" );
    }

    public static long calculateMaxSingleElementSize(VarSizedRingBuffer vsbr) {
        long eUpperBound = vsbr.max - VarSizedRingBuffer.START;
        int updatedLiSize = LIbae.generateLI(eUpperBound).length;
        int liSize;
        do {
            liSize = updatedLiSize;
            updatedLiSize = LIbae.generateLI(eUpperBound - liSize).length;
        } while (updatedLiSize != liSize);
        return eUpperBound - liSize;
    }
}
