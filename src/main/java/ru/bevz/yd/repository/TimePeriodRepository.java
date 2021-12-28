package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.TimePeriod;

@Repository
public interface TimePeriodRepository extends JpaRepository<TimePeriod, Long> {

}
