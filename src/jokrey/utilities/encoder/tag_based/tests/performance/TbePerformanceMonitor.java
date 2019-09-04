package jokrey.utilities.encoder.tag_based.tests.performance;

import jokrey.utilities.debug_analysis_helper.AverageCallTimeMarker;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.transparent_storage.TransparentStorage;
import jokrey.utilities.encoder.type_transformer.TypeToFromRawTransformer;

import java.util.Iterator;

public class TbePerformanceMonitor<SF> implements TagBasedEncoder<SF> {
    private final TagBasedEncoder<SF> monitoring_subject;
    public TbePerformanceMonitor(TagBasedEncoder<SF> encoder) {
        monitoring_subject=encoder;
    }

    @Override public boolean exists(String tag) {
        AverageCallTimeMarker.mark_call_start("exists");
        boolean result = monitoring_subject.exists(tag);
        AverageCallTimeMarker.mark_call_end("exists");
        return result;
    }

    @Override public long length(String tag) {
        AverageCallTimeMarker.mark_call_start("length");
        long result = monitoring_subject.length(tag);
        AverageCallTimeMarker.mark_call_end("length");
        return result;
    }

    @Override public String[] getTags() {
        AverageCallTimeMarker.mark_call_start("getTags");
        String[] result = monitoring_subject.getTags();
        AverageCallTimeMarker.mark_call_end("getTags");
        return result;
    }

    @Override public TbePerformanceMonitor<SF> clear() {
        AverageCallTimeMarker.mark_call_start("clear");
        monitoring_subject.clear();
        AverageCallTimeMarker.mark_call_end("clear");
        return this;
    }

    @Override public boolean deleteEntry_noReturn(String tag) {
        AverageCallTimeMarker.mark_call_start("deleteEntry_noReturn");
        boolean result = monitoring_subject.deleteEntry_noReturn(tag);
        AverageCallTimeMarker.mark_call_end("deleteEntry_noReturn");
        return result;
    }

    @Override public TbePerformanceMonitor<SF> addEntry_nocheck(String tag, SF entry) {
        AverageCallTimeMarker.mark_call_start("addEntry_nocheck");
        monitoring_subject.addEntry_nocheck(tag, entry);
        AverageCallTimeMarker.mark_call_end("addEntry_nocheck");
        return this;
    }

    @Override public SF getEntry(String tag) {
        AverageCallTimeMarker.mark_call_start("getEntry");
        SF result = monitoring_subject.getEntry(tag);
        AverageCallTimeMarker.mark_call_end("getEntry");
        return result;
    }

    @Override public SF deleteEntry(String tag) {
        AverageCallTimeMarker.mark_call_start("deleteEntry");
        SF result = monitoring_subject.deleteEntry(tag);
        AverageCallTimeMarker.mark_call_end("deleteEntry");
        return result;
    }

    @Override public TypeToFromRawTransformer<SF> createTypeTransformer() {
        AverageCallTimeMarker.mark_call_start("getTypeTransformer");
        TypeToFromRawTransformer<SF> result = monitoring_subject.getTypeTransformer();
        AverageCallTimeMarker.mark_call_end("getTypeTransformer");
        return result;
    }

    @Override public TbePerformanceMonitor<SF> readFromEncoded(SF encoded_raw) {
        AverageCallTimeMarker.mark_call_start("readFromEncoded");
        monitoring_subject.readFromEncoded(encoded_raw);
        AverageCallTimeMarker.mark_call_end("readFromEncoded");
        return this;
    }

    @Override public SF getEncoded() {
        AverageCallTimeMarker.mark_call_start("getEncoded");
        SF result = monitoring_subject.getEncoded();
        AverageCallTimeMarker.mark_call_end("getEncoded");
        return result;
    }

    @Override public int hashCode() {
        return monitoring_subject.hashCode(); // not regarded - because it is allowed to not be supported
    }

    @Override public boolean equals(Object o) {
        return o instanceof TbePerformanceMonitor && monitoring_subject.equals(((TbePerformanceMonitor)o).monitoring_subject); // not regarded - because it is allowed to not be supported
    }

    @Override public Iterator<TaggedEntry<SF>> iterator() {
        return monitoring_subject.iterator(); //not regarded - would make no sense
    }

    @Override public TransparentStorage<SF> getRawStorageSystem() {
        return monitoring_subject.getRawStorageSystem(); //not regarded - would make no sense
    }

    @Override public byte[] getEncodedBytes() {
        AverageCallTimeMarker.mark_call_start("getEncodedBytes");
        byte[] result = monitoring_subject.getEncodedBytes();
        AverageCallTimeMarker.mark_call_end("getEncodedBytes");
        return result;
    }

    @Override public void readFromEncodedBytes(byte[] encoded_bytes) {
        AverageCallTimeMarker.mark_call_start("readFromEncodedBytes");
        monitoring_subject.readFromEncodedBytes(encoded_bytes);
        AverageCallTimeMarker.mark_call_end("readFromEncodedBytes");
    }

    @Override public String getEncodedString() {
        AverageCallTimeMarker.mark_call_start("getEncodedString");
        String result = monitoring_subject.getEncodedString();
        AverageCallTimeMarker.mark_call_end("getEncodedString");
        return result;
    }

    @Override public void readFromEncodedString(String encoded_string) {
        AverageCallTimeMarker.mark_call_start("readFromEncodedString");
        monitoring_subject.readFromEncodedString(encoded_string);
        AverageCallTimeMarker.mark_call_end("readFromEncodedString");
    }
}
