package evgenykravtsov.habitbreaking.domain.os;

import java.util.Calendar;

public class Utils {

    public static long getCurrentTimeUnixSeconds() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }
}
