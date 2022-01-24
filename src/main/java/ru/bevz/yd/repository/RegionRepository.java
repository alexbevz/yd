package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    boolean existsRegionsByNumberRegion(int number);

    Region getRegionByNumberRegion(int number);

}
