package jokrey.utilities.date_time;

/**
 * ExactDateTime's are comparable.
 */
public interface TimeComparable extends Comparable<TimeComparable> {
    boolean isAfter(ExactDateTime relativeTime);
    boolean isBefore(ExactDateTime relativeTime);
}
