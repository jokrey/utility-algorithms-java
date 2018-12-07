package jokrey.utilities.date_time;

import java.io.Serializable;
import java.util.Calendar;

/**
 * maybe remove call to java.util.Calendar. Replace with math based on System.currentTimeMillis()
 *
 * Stores a single integer and interprets it as a time in a day(i.e seconds greater than 0 hours and smaller than 24)
 *
 * immutable
 */
public class ExactTimeOfDay implements Serializable {
    private final int millisecondsSincePhoneTime0000;
    public ExactTimeOfDay(int millisecondsSincePhoneTime0000) {
        this.millisecondsSincePhoneTime0000 = millisecondsSincePhoneTime0000;
        if(millisecondsSincePhoneTime0000>24*60*60*1000)
            throw new IllegalArgumentException("can't have more than "+(24*60*60*1000)+" ms in a day..");
        else if(millisecondsSincePhoneTime0000<0)
            throw new IllegalArgumentException("can't have less than 0 ms in a day..");
    }
    public ExactTimeOfDay(int hour, int minute, int second) {
        this(hour, minute, second, 0);
    }
    public ExactTimeOfDay(int hour, int minute, int second, int millisecond) {
        this(hour*60*60*1000 + minute*60*1000 + second*1000 + millisecond);
    }
    public int getHourInDay() {
        return millisecondsSincePhoneTime0000 / (60*60*1000);
    }
    public int getMinuteInHour() {
        return ( millisecondsSincePhoneTime0000 % (60*60*1000) ) / (60*1000);
    }
    public int getSecondInMinute() {
        return ( ( millisecondsSincePhoneTime0000 % (60*60*1000) ) % (60*1000) ) / 1000;
    }
    public int getMillisecondInSecond() {
        return ( ( millisecondsSincePhoneTime0000 % (60*60*1000) ) % (60*1000) ) % 1000;
    }
    public int getMillisSince00() {
        return millisecondsSincePhoneTime0000;
    }

    public static ExactTimeOfDay now() {
        Calendar c = Calendar.getInstance(); //now

        Calendar m = Calendar.getInstance(); //midnight
        m.set(Calendar.HOUR_OF_DAY, 0);
        m.set(Calendar.MINUTE, 0);
        m.set(Calendar.SECOND, 0);
        m.set(Calendar.MILLISECOND, 0);

        int diff = (int) (c.getTimeInMillis() - m.getTimeInMillis()) ;
        return new ExactTimeOfDay(diff);
    }
    public ExactTimeOfDay plusSeconds(int seconds) {
        return new ExactTimeOfDay(millisecondsSincePhoneTime0000+(seconds*1000));
    }
    public ExactTimeOfDay minusSeconds(int seconds) {
        return new ExactTimeOfDay(millisecondsSincePhoneTime0000-(seconds*1000));
    }

    @Override public boolean equals(Object obj) {
        try {
            ExactTimeOfDay et = (ExactTimeOfDay)obj;
            return millisecondsSincePhoneTime0000 == et.millisecondsSincePhoneTime0000;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override public String toString() {
        return String.format("%02d", getHourInDay()) + ":"+String.format("%02d", getMinuteInHour())+":"+String.format("%02d", getSecondInMinute())/*+":"+String.format("%02d", getMillisecondInSecond())*/;
    }

    public boolean isAfter(ExactTimeOfDay time) {
        return millisecondsSincePhoneTime0000 > time.millisecondsSincePhoneTime0000;
    }
    public boolean isBefore(ExactTimeOfDay time) {
        return millisecondsSincePhoneTime0000 < time.millisecondsSincePhoneTime0000;
    }
}