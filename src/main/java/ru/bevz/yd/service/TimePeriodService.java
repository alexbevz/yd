package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.model.TimePeriod;
import ru.bevz.yd.repository.TimePeriodRepository;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TimePeriodService {

    @Autowired
    private TimePeriodRepository timePeriodRep;

    private TimePeriod addIfNotExistsTimePeriod(TimePeriod tp) {
        LocalTime from = tp.getFrom();
        LocalTime to = tp.getTo();

        if (timePeriodRep.existsByFromAndTo(from, to)) {
            tp = timePeriodRep.getTimePeriodByFromAndTo(from, to);
        } else {
            timePeriodRep.save(tp);
        }

        return tp;
    }

    public Set<TimePeriod> addIfNotExistsTimePeriods(Set<TimePeriod> tps) {
        return tps
                .stream()
                .map(this::addIfNotExistsTimePeriod)
                .collect(Collectors.toSet());
    }

}
