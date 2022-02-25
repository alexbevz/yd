package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bevz.yd.model.Rate;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Integer> {

    Optional<Rate> getRateByValue(float value);

}
