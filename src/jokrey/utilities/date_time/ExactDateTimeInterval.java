package jokrey.utilities.date_time;

/**
 * Stores two ExactDateTime's and provides certain methods to interpret them as an interval
 *
 * immutable
 */
public class ExactDateTimeInterval implements TimeComparable {
    private final ExactDateTime interval_start;
    private final ExactDateTime interval_end;
    public ExactDateTimeInterval(ExactDateTime e1, ExactDateTime e2) {
        if(e1.isBefore(e2)) {
            interval_start = e1;
            interval_end = e2;
        } else {
            interval_start = e2;
            interval_end = e1;
        }
    }

    public boolean isInInterval_incl(ExactDateTime edt) {
        return edt.equals(interval_start) || edt.equals(interval_end) || isInInterval_excl(edt);
    }
    public boolean isInInterval_excl(ExactDateTime edt) {
        return (edt.isAfter(interval_start) && edt.isBefore(interval_end));
    }
    @Override public boolean isAfter(ExactDateTime relativeTime) {
        return interval_start.isAfter(relativeTime);
    }

    @Override public boolean isBefore(ExactDateTime relativeTime) {
        return interval_end.isBefore(relativeTime);
    }

    //Comparing based on start..
    @Override public int compareTo(TimeComparable o2) {
        if (o2.isAfter(interval_start))
            return -1;
        if (o2.isBefore(interval_start))
            return 1;
        return 0;
    }
    public int compareTo(ExactDateTimeInterval o) {
        return interval_start.compareTo(o.interval_start);
    }

    @Override public boolean equals(Object o) {
        if(o instanceof ExactDateTimeInterval) {
            return interval_start.equals(((ExactDateTimeInterval)o).interval_start) &&
                    interval_end.equals(((ExactDateTimeInterval)o).interval_end);
        }
        return false;
    }

    @Override public String toString() {
        return interval_start + " - " + interval_end;
    }
}
