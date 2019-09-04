package jokrey.utilities.date_time;

import jokrey.utilities.encoder.EncodableAsBytes;
import jokrey.utilities.encoder.EncodableAsString;
import jokrey.utilities.encoder.tag_based.TagBasedEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.bytes.LITagBytesEncoder;
import jokrey.utilities.encoder.tag_based.implementation.paired.length_indicator.string.LITagStringEncoder;
import jokrey.utilities.encoder.as_union.li.string.LIseLegacy_Fixed;

import java.io.Serializable;

/**
 * Does nothing more that store an ExactDate and an ExactTimeOfDay.
 * Then it provides certain functionality to access these dates.
 * Does not do anything complex. Ignores timezones and anything complex.
 *
 * immutable
 */
public class ExactDateTime implements TimeComparable, Serializable, EncodableAsString, EncodableAsBytes {
    private ExactDate date; public ExactDate getDate() { return date; }
    private ExactTimeOfDay time; public ExactTimeOfDay getTime() { return time; }
    public ExactDateTime() {}
    public ExactDateTime(String encoded_str) {
        readFromEncodedString(encoded_str);
    }
    public ExactDateTime(ExactDate date, ExactTimeOfDay time) {
        this.date=date;
        this.time=time;
    }

    public static ExactDateTime todayandnow() {
        return new ExactDateTime(ExactDate.today(), ExactTimeOfDay.now());
    }

    public boolean isAfter(ExactDateTime relativeTime) {
        return date.isAfter(relativeTime.date) || (date.equals(relativeTime.date) && time.isAfter(relativeTime.time));
    }
    public boolean isBefore(ExactDateTime relativeTime) {
        return date.isBefore(relativeTime.date) || (date.equals(relativeTime.date) && time.isBefore(relativeTime.time));
    }

    @Override public boolean equals(Object obj) {
        try {
            ExactDateTime edt = (ExactDateTime)obj;
            return date.equals(edt.date) && time.equals(edt.time);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override public int compareTo(TimeComparable o2) {
        if(o2.isAfter(this))
            return -1;
        if(o2.isBefore(this))
            return 1;
        return 0;

//        int date_compare = date.compareTo(o2.date);
//        if(date_compare==0)
//            return time.getMillisSince00() - o2.time.getMillisSince00();
//        else
//            return date_compare;
    }

    @Override public String toString() {
        return date.toString() + " - "+time.toString();
    }


    public ExactDateTime getAdded(long ms_toAdd) {
        return getAdded((int) ( ms_toAdd / (24*60*60*1000) ),
                (int) ( (ms_toAdd % (24*60*60*1000)) / (60*60*1000) ),
                (int) ( ( (ms_toAdd % (24*60*60*1000)) % (60*60*1000) ) / (60*1000) ),
                (int) ( ( ( (ms_toAdd % (24*60*60*1000)) % (60*60*1000) ) % (60*1000) ) / 1000) );
    }
    public ExactDateTime getAdded(int days, int hours, int minutes, int seconds) {
        int addedDays = 0;
        int seconds_to_add = hours * 60 * 60 + minutes * 60 + seconds;
        ExactTimeOfDay tempTime=time;
        while(seconds_to_add!=0) {
            try {
                tempTime=tempTime.plusSeconds(seconds_to_add);
                seconds_to_add=0;
            } catch (Exception ex) {
                addedDays++;
                seconds_to_add = (seconds_to_add + tempTime.getMillisSince00() /1000) - 24*60*60;
                tempTime=new ExactTimeOfDay(0);
            }
        }
        ExactDate tempDate = date.plusDays(days+addedDays);
        return new ExactDateTime(tempDate, tempTime);
    }
    public ExactDateTime getSubtracted(long ms_toSubtract) {
        return getSubtracted((int) ( ms_toSubtract / (24*60*60*1000) ),
                (int) ( (ms_toSubtract % (24*60*60*1000)) / (60*60*1000) ),
                (int) ( ( (ms_toSubtract % (24*60*60*1000)) % (60*60*1000) ) / (60*1000) ),
                (int) ( ( ( (ms_toSubtract % (24*60*60*1000)) % (60*60*1000) ) % (60*1000) ) / 1000) );
    }
    public ExactDateTime getSubtracted(int days, int hours, int minutes, int seconds) {
        int addedDays = 0;
        int seconds_to_add = hours * 60 * 60 + minutes * 60 + seconds;
        ExactTimeOfDay tempTime=time;
        while(seconds_to_add!=0) {
            try {
                tempTime=tempTime.minusSeconds(seconds_to_add);
                seconds_to_add=0;
            } catch (Exception ex) {
                addedDays++;
                seconds_to_add = (seconds_to_add + tempTime.getMillisSince00() /1000) - 24*60*60;
                tempTime=new ExactTimeOfDay(0);
            }
        }
        ExactDate tempDate = date.minusDays(days+addedDays);
        return new ExactDateTime(tempDate, tempTime);
    }

    public static int[] getTimeDifferenceOf(ExactDateTime edt1, ExactDateTime edt2) {
        if(edt1.equals(edt2)) {
            return new int[] {0,0,0,0};
        } else if(edt1.isAfter(edt2)) {
            return getTimeDifferenceOf(edt2, edt1);
        } else {//edt1.isAfter(edt2)
            int days = edt1.date.countDaysTo(edt2.date, 10);
            if (days == -1) return new int[] {10 * 365 + 2, 0,0,0};//+2 for aprox leap years
            int hours = edt2.time.getHourInDay() - edt1.time.getHourInDay();
            int minutes = edt2.time.getMinuteInHour() - edt1.time.getMinuteInHour();
            int seconds = edt2.time.getSecondInMinute() - edt1.time.getSecondInMinute();
            if (seconds < 0) {
                minutes--;
                seconds = 60 + seconds;
            }
            if (minutes < 0) {
                hours--;
                minutes = 60 + minutes;
            }
            if (hours < 0) {
                days--;
                hours = 24 + hours;
            }

            return new int[] {days, hours, minutes, seconds};
        }
    }
    public static String getTimeDifferenceOf_asTextualRepresentation(ExactDateTime edt1, ExactDateTime edt2, boolean shorten) {
        int[] d_h_m_s = getTimeDifferenceOf(edt1, edt2);

        String building = "";
        if (d_h_m_s[0] > 0) {
            building += String.format("%02d", d_h_m_s[0]) + " day";
            if (d_h_m_s[0] > 1) building += "s";
            building += ", ";
        }
        if (d_h_m_s[1] > 0) {
            building += String.format("%02d", d_h_m_s[1]) + " hour";
            if (d_h_m_s[1] > 1) building += "s";
            building += ", ";
        }
        if (d_h_m_s[2] > 0 && (!shorten || d_h_m_s[0] == 0)) {
            building += String.format("%02d", d_h_m_s[2]) + " minute";
            if (d_h_m_s[2] > 1) building += "s";
            building += ", ";
        }
        if (/*d_h_m_s[3] > 0 && */(!shorten || (d_h_m_s[0] == 0 && d_h_m_s[1] == 0))) {
            building += String.format("%02d", d_h_m_s[3]) + " second";
            if (d_h_m_s[3] != 1) building += "s";
            building += ", ";
        }
        if (!building.isEmpty())
            building = building.substring(0, building.length() - 2);
        return building;
    }
    public String getTimeDifferenceTo_asTextualRepresentation(ExactDateTime edt2, boolean shorten) {
        if(edt2==null)return "given time null";
        if(this.equals(edt2)) {
            return "now";
        } else if(isAfter(edt2)) {
            return "in "+getTimeDifferenceOf_asTextualRepresentation(this, edt2, shorten);
        } else {
            return getTimeDifferenceOf_asTextualRepresentation(this, edt2, shorten) + " ago";
        }
    }

    public String asFileTimeStamp() {
        StringBuilder b = new StringBuilder(13); //always size 13..
        b.append(date.getYear()).delete(0,2);
        b.append(String.format("%02d",date.getMonth())).append(String.format("%02d",date.getDay()));
        b.append("_");
        b.append(String.format("%02d",time.getHourInDay())).append(String.format("%02d",time.getMinuteInHour())).append(String.format("%02d",time.getSecondInMinute()));
        return b.toString();
    }


    @Override public String getEncodedString() {
        return encode_into(new LITagStringEncoder(new LIseLegacy_Fixed())).getEncodedString();
    }
    @Override public void readFromEncodedString(String dt_encoded) {
        decode_from(new LITagStringEncoder(new LIseLegacy_Fixed(dt_encoded)));
    }
    @Override public byte[] getEncodedBytes() {
        return encode_into(new LITagBytesEncoder()).getEncodedBytes();
    }
    @Override public void readFromEncodedBytes(byte[] encoded_bytes) {
        decode_from(new LITagBytesEncoder(encoded_bytes));
    }
    private TagBasedEncoder<?> encode_into(TagBasedEncoder<?> encoder) {
        encoder.addEntryT("date_year", date.getYear());
        encoder.addEntryT("date_month", date.getMonth());
        encoder.addEntryT("date_day", date.getDay());
        encoder.addEntryT("time", time.getMillisSince00());
        return encoder;
    }
    private void decode_from(TagBasedEncoder<?> encoder) {
        date = new ExactDate(encoder.getEntryT("date_day", 1), encoder.getEntryT("date_month", 1), encoder.getEntryT("date_year", 1970));
        time = new ExactTimeOfDay(encoder.getEntryT("time",0));
    }
}
