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
    private TimePeriodRepository timePeriodRepository;

    private TimePeriod addIfNotExistsTimePeriod(TimePeriod timePeriod) {
        LocalTime from = timePeriod.getFrom();
        LocalTime to = timePeriod.getTo();

        if (timePeriodRepository.existsByFromAndTo(from, to)) {
            timePeriod = timePeriodRepository.getTimePeriodByFromAndTo(from, to);
        } else {
            timePeriodRepository.save(timePeriod);
        }

        return timePeriod;
    }

    public Set<TimePeriod> addIfNotExistsTimePeriods(Set<TimePeriod> timePeriodList) {
        return timePeriodList
                .stream()
                .map(this::addIfNotExistsTimePeriod)
                .collect(Collectors.toSet());
    }

}
