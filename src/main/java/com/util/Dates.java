package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 12:36 AM
 */
public class Dates {
    private final Calendar calendar;

    public Dates() {
        this(Calendar.getInstance(TimeZone.getDefault()));
    }

    public Dates(Calendar calendar) {
        this.calendar = calendar;
    }

    public Dates(String data) {
        this(data, "yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    public Dates(String data, String format) {
        this(data, format, "UTC");
    }

    public Dates(String date, String format, String timeZone) {
        try {
            Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            long gmtTime = gmt.getTime().getTime();
            long timezoneAlteredTime = gmtTime
                    + TimeZone.getTimeZone(timeZone).getRawOffset();
            this.calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            calendar.setTimeInMillis(new SimpleDateFormat(format).parse(date).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Calendar calendar() {
        return (Calendar) this.calendar.clone();
    }

    public long difference(final Dates dates, final int type) {
        return difference(dates.calendar(), type);
    }

    public long difference(Calendar endDate, int type) {
        final Calendar start = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        start.set(Calendar.YEAR, this.calendar.get(Calendar.YEAR));
        start.set(Calendar.MONTH, this.calendar.get(Calendar.MONTH));
        start.set(Calendar.DAY_OF_MONTH, this.calendar.get(Calendar.DAY_OF_MONTH));
        start.set(Calendar.HOUR_OF_DAY, this.calendar.get(Calendar.HOUR_OF_DAY));
        start.set(Calendar.MINUTE, this.calendar.get(Calendar.MINUTE));
        start.set(Calendar.SECOND, this.calendar.get(Calendar.SECOND));
        start.set(Calendar.MILLISECOND, this.calendar.get(Calendar.MILLISECOND));
        final Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        end.set(Calendar.YEAR, endDate.get(Calendar.YEAR));
        end.set(Calendar.MONTH, endDate.get(Calendar.MONTH));
        end.set(Calendar.DAY_OF_MONTH, endDate.get(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, endDate.get(Calendar.HOUR_OF_DAY));
        end.set(Calendar.MINUTE, endDate.get(Calendar.MINUTE));
        end.set(Calendar.SECOND, endDate.get(Calendar.SECOND));
        end.set(Calendar.MILLISECOND, endDate.get(Calendar.MILLISECOND));
        long between = 0;
        if (start.before(end)) {
            while (start.before(end)) {
                start.add(type, 1);
                between -= 1;
            }
        } else {
            while (start.after(end)) {
                start.add(type, -1);
                between += 1;
            }
        }


        return between;
    }
}
