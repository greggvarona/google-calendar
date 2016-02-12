package jp.co.isr.gvarona.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gregg varona on 2/10/2016.
 */
public class DateUtils {

    public static Date add(Date date, int noOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date result = calendar.getTime();
        return result;
    }

    public static String format(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /*
     * Can actually have more helper functions when required.
     */
}
