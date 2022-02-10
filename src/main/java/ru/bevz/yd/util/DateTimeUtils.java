package ru.bevz.yd.util;

import lombok.NoArgsConstructor;
import ru.bevz.yd.exception.ConversionTPException;
import ru.bevz.yd.model.TimePeriod;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@NoArgsConstructor
public class DateTimeUtils {

    public static TimePeriod toTP(String str) {
        TimePeriod tp = new TimePeriod();

        try {
            List<LocalTime> localTimes = Arrays
                    .stream(str.split("-"))
                    .map(LocalTime::parse)
                    .toList();

            tp.setFrom(localTimes.get(0));
            tp.setTo(localTimes.get(1));

            return tp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConversionTPException(str);
        }
    }

    public static String toStringTP(TimePeriod tp) {
        return tp.getFrom().toString() + "-" + tp.getTo();
    }

    public static Optional<TimePeriod> findTPForTime(Set<TimePeriod> tps, LocalDateTime time) {
        return findTPForTime(tps, time.toLocalTime());
    }

    public static Optional<TimePeriod> findTPForTime(Set<TimePeriod> tps, LocalTime time) {
        return tps.stream()
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
