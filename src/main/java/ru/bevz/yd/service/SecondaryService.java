package ru.bevz.yd.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.TimePeriod;
import ru.bevz.yd.repository.RegionRepository;
import ru.bevz.yd.repository.TimePeriodRepository;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SecondaryService {

    @Autowired
    private RegionRepository regionRep;

    @Autowired
    private TimePeriodRepository timePeriodRep;

    public Set<Region> getOrSaveRegionsByNumber(Set<Integer> numbers) {
        return numbers
                .stream()
                .map(this::getOrSaveRegionByNumber)
                .collect(Collectors.toSet());
    }

    public Region getOrSaveRegionByNumber(int number) {
        return regionRep.findRegionByNumber(number)
                .orElseGet(() -> regionRep.save(new Region().setNumber(number)));
    }

    public Set<TimePeriod> getOrSaveTimePeriodsByString(Set<String> timePeriods) {
        return timePeriods
                .stream()
                .map(this::getOrSaveTimePeriodByString)
                .collect(Collectors.toSet());
    }

    public TimePeriod getOrSaveTimePeriodByString(String tp) {

        TimePeriod timePeriod = DateTimeUtils.toTP(tp);

        return timePeriodRep.findTimePeriodByFromAndTo(timePeriod.getFrom(), timePeriod.getTo())
                .orElseGet(() -> timePeriodRep.save(timePeriod));
    }
}
