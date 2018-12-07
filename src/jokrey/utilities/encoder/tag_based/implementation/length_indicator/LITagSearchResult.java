package jokrey.utilities.encoder.tag_based.implementation.length_indicator;

/**
 * INTERNAL USAGE
 *
 * @author jokrey
 */
public class LITagSearchResult { //to not use a non descriptive long[].. Should be of similar performance (bottleneck heap allocation for both..)
    public final long entry_start_index;
    public final long entry_end_index;
    public final long raw_storage_start_index;
    public LITagSearchResult(long entry_start_index, long entry_end_index, long raw_storage_start_index) {
        this.entry_start_index = entry_start_index;
        this.entry_end_index = entry_end_index;
        this.raw_storage_start_index = raw_storage_start_index;
        if(! (raw_storage_start_index <= entry_start_index && entry_start_index <= entry_end_index))
            throw new IllegalArgumentException(toString());
    }
    public boolean largerThan(LITagSearchResult o) {
        return raw_storage_start_index > o.raw_storage_start_index; //other don't have to be compared. Should they matter we have a larger issue at hand.....
    }
    public long total_length() {
        return entry_end_index - raw_storage_start_index;
    }
    @Override public String toString() {
        return "LITagSearchResult{" + "entry_start_index=" + entry_start_index + ", entry_end_index=" + entry_end_index + ", raw_storage_start_index=" + raw_storage_start_index + '}';
    }

    public LITagSearchResult minus(long minus) {
        return new LITagSearchResult(entry_start_index - minus, entry_end_index - minus, raw_storage_start_index - minus);
    }
}