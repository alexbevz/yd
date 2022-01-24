package ru.bevz.yd.util;

import ru.bevz.yd.model.TimePeriod;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static TimePeriod toTimePeriod(String str) {

        TimePeriod timePeriod = new TimePeriod();

        try {
            List<LocalTime> localTimes = Arrays
                    .stream(str.split("-"))
                    .map(LocalTime::parse)
                    .toList();

            timePeriod.setFrom(localTimes.get(0));
            timePeriod.setTo(localTimes.get(1));

            return timePeriod;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String toStringTP(TimePeriod timePeriod) {
        return timePeriod.getFrom().toString() + "-" + timePeriod.getTo();
    }

}
