package ru.bevz.yd.util;

import ru.bevz.yd.model.TimePeriod;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static TimePeriod toTP(String str) {

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

    public static Optional<TimePeriod> findTPForTime(Set<TimePeriod> timePeriodList, LocalDateTime time) {
        return findTPForTime(timePeriodList, time.toLocalTime());
    }

    public static Optional<TimePeriod> findTPForTime(Set<TimePeriod> timePeriodList, LocalTime time) {
        return timePeriodList
                .stream()
                .filter(tp -> isTimeInTP(tp, time))
                .min(Comparator.comparing(TimePeriod::getFrom));
    }

    public static boolean isTimeInTP(TimePeriod tp, LocalDateTime time) {
        return isTimeInTP(tp, time.toLocalTime());
    }

    public static boolean isTimeInTP(TimePeriod tp, LocalTime time) {
        return tp.getFrom().isBefore(time) && tp.getTo().isAfter(time);
    }

}
