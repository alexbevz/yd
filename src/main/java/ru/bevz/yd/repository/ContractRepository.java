package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.StatusContract;

import java.util.List;
import java.util.Set;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> getAllByCourierAndStatus(Courier courier, StatusContract statusContract);

    List<Contract> getAllByStatusAndRegionInAndWeightLessThanEqual(
            StatusContract statusContract,
            Set<Region> regions,
            float weight
    );

}
