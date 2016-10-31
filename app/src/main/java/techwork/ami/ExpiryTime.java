package techwork.ami;

import android.os.CountDownTimer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tataf on 30-10-2016.
 */

public class ExpiryTime {

    public ExpiryTime(){

    }
    public long getTimeDiference(String futureDate) {


        long diff = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat(Config.DATETIME_FORMAT_ANDROID);
            Date futureTime = format.parse(futureDate);
            Date currentTime = new Date();

            long fTime = futureTime.getTime();
            long cTime = currentTime.getTime();
            diff = fTime - cTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff;

    }

}
