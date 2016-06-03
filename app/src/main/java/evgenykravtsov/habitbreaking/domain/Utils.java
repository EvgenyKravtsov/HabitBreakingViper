package evgenykravtsov.habitbreaking.domain;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static long getCurrentTimeUnixSeconds() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    public static long getDayFromDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() / 1000;
    }
}
