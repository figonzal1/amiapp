package techwork.ami;

import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by tataf on 28-10-2016.
 */

public class DateDifference {

    public DateDifference(){

    }
    public long getDifference(Date startDate, Date endDate){

        long different = Math.abs(endDate.getTime()-startDate.getTime());

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long days = different / daysInMilli;
        different = different % daysInMilli;

        long hours = different / hoursInMilli;
        different = different % hoursInMilli;

        long minutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long seconds = different / secondsInMilli;

        return DateUtils.DAY_IN_MILLIS * days +
                DateUtils.HOUR_IN_MILLIS * hours +
                DateUtils.MINUTE_IN_MILLIS * minutes +
                DateUtils.SECOND_IN_MILLIS * seconds;
    }
}
