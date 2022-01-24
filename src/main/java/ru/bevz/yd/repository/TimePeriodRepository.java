package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.TimePeriod;

import java.time.LocalTime;

@Repository
public interface TimePeriodRepository extends JpaRepository<TimePeriod, Integer> {

    boolean existsByFromAndTo(LocalTime from, LocalTime to);

    TimePeriod getTimePeriodByFromAndTo(LocalTime from, LocalTime to);

}
