package jokrey.utilities.date_time;

import java.io.Serializable;
import java.util.Calendar;

/**
 * maybe remove call to java.util.Calendar. Replace with math based on System.currentTimeMillis()
 *
 * Does nothing more that store 3 integers for a date. (year, month, day).
 * Then it provides certain functionality to access these dates.
 * Does not do anything complex. Ignores timezones and anything complex.
 *
 * immutable
 */
public class ExactDate implements Serializable {
    public ExactDate(int day, int month, int year) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }
    
    private int day = 1;
    public int getDay() {return day;}
    private void setDay(int nDay) {
        setDay(nDay, false);
    }
    private void setDay(int nDay, boolean resetTo1) {
        day = nDay;
        if (day<1)day = 1;
        else if(day>getDaysInMonth(year, month)) {
            day=resetTo1?1:getDaysInMonth(year, month);
        }
        /*day = nDay;
        if (day<1)day = 1;
        else if(day>28) {
            if(day>31)day=resetTo1?1:31;
            if(day==31) {
                if(month==2||month==4||month==6||month==9||month==11)day=resetTo1?1:30;
            }
            if(day>=29&&month==2) {
                if(isLeapYear(year)) {
                    if(day>29) day=resetTo1?1:29;
                } else {
                    day=resetTo1?1:28;
                }
            }
        }*/
    }
    public static int getDaysInMonth(int year, int month) {
        if(month==2)
            return isLeapYear(year)?29:28;
        else if(month==4||month==6||month==9||month==11)
            return 30;
        else
            return 31;
    }
    public static boolean isLeapYear(int year) {
        return year%4==0 && (year%100!=0 || year%400==0);
    }
    public static int getFirstDayInMonthWithWeekday(int weekday, int month, int year) {
        ExactDate date = new ExactDate(1, month, year);
        while(weekday != date.getDayOfWeek()) {
            date=date.plusDays(1);
        }
        return date.getDay();
    }
    public static int getLastDayInMonthWithWeekday(int weekday, int month, int year) {
        ExactDate date = new ExactDate(getDaysInMonth(year, month), month, year);
        while(weekday != date.getDayOfWeek()) {
            date=date.minusDays(1);
        }
        return date.getDay();
    }
    private int month = 1;
    public int getMonth() {return month;}
    private void setMonth(int nMonth) {
        month = nMonth;
        if (month<1||month>12)month = 1;
    }
    private int year = 1;
    public int getYear() {return year;}
    private void setYear(int nYear) {
        year = nYear;
    }

    //from 0(Monday), to 6(Sunday)
    public int getDayOfWeek() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK)-2;
        day_of_week=day_of_week<0?day_of_week+7:day_of_week;
        return day_of_week;
    }

    public static ExactDate today() {
        Calendar c = Calendar.getInstance();
        return new ExactDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
    }
    public boolean isToday() {
        return this.equals(today());
    }

    @Override public String toString() {
        return String.format("%02d", day)+"."+String.format("%02d", month)+"."+year;
    }
    @Override public boolean equals(Object o) {
        try {
            ExactDate d = (ExactDate)o;
            return d.year==year&&d.month==month&&d.day==day;
        } catch(Exception ex) {
            return false;
        }
    }


    private void nextDay() {
        if(day==1) {
            day++;
        } else {
            setDay(day+1, true);
            if(day==1) {
                if(month==12) {
                    month=1;
                    year++;
                } else
                    month++;
            }
        }
    }
    private void previousDay() {
        if(day>1)day--;
        else {
            if(month>1) {
                month--;
                setDay(Integer.MAX_VALUE);
            } else {
                year--;month=12;day=31;
            }
        }
    }
    public ExactDate getDateInMonthBefore() {
        return new ExactDate(day, month-1<1?12:1, month-1<1?year-1:year);
    }



    public int countDaysTo(ExactDate relativeDate, int years_tops) {
        if(Math.abs(year - relativeDate.year) > years_tops) {//otherwise might take "a while"
            return -1;
        }

        ExactDate tempDate = new ExactDate(day, month, year);
        int count = 0;
        if(tempDate.isBefore(relativeDate)) {
            while(!tempDate.equals(relativeDate)) {
                count++;
                tempDate.nextDay();
            }
        } else {
            while(!tempDate.equals(relativeDate)) {
                count++;
                tempDate.previousDay();
            }
        }
        return count;
    }

    public ExactDate minusDays(int days) {
        ExactDate tempDate = new ExactDate(day, month, year);

        for(int i=0;i<days;i++)
            tempDate.previousDay();

        return tempDate;
    }
    public ExactDate plusDays(int days) {
        ExactDate tempDate = new ExactDate(day, month, year);

        for(int i=0;i<days;i++)
            tempDate.nextDay();

        return tempDate;
    }


    public boolean isAfter(ExactDate relativeDate) {
        return compareTo(relativeDate)>0;
    }
    public boolean isBefore(ExactDate relativeDate) {
        return compareTo(relativeDate)<0;
    }
    public int compareTo(ExactDate o) {
        if (year!=o.year)
            return year-o.year;
        else {
            if (month!=o.month)
                return month-o.month;
            else {
                if (day!=o.day)
                    return day-o.day;
                else {
                    return 0;
                }
            }
        }
    }
}