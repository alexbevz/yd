package ru.bevz.yd.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.CompletedCourierRepository;
import ru.bevz.yd.repository.RateRepository;
import ru.bevz.yd.repository.RegionRepository;
import ru.bevz.yd.repository.TimePeriodRepository;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.bevz.yd.constant.GlobalConstant.GENERAL_RATE;

@Service
public class SecondaryService {

    @Autowired
    private RegionRepository regionRep;

    @Autowired
    private TimePeriodRepository timePeriodRep;

    @Autowired
    private CompletedCourierRepository completedCourierRep;

    @Autowired
    private RateRepository rateRep;

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

    public Rate getOrSaveRate(float value) {
        return rateRep.getRateByValue(value)
                .orElseGet(() -> rateRep.save(new Rate().setValue(value)));
    }

    public CompletedCourier getOrSaveCompletedCourier(int courierId, int ratioId) {
        Rate rate = getOrSaveRate(GENERAL_RATE);
        return completedCourierRep.getCompletedCourierByOptions(courierId, ratioId, rate.getId())
                .orElseGet(() -> completedCourierRep.save(
                                new CompletedCourier()
                                        .setCourier(new Courier().setId(courierId))
                                        .setRate(rate)
                                        .setRatio(new Ratio().setValue(ratioId))
                        )
                );
    }
}
