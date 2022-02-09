package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.TimePeriod;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TimePeriodRepository extends JpaRepository<TimePeriod, Integer> {

    Optional<TimePeriod> findTimePeriodByFromAndTo(LocalTime from, LocalTime to);

}
