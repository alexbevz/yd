package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.StatusContract;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> getAllByCourierAndStatus(Courier courier, StatusContract statusContract);

    @Query(
            value = "SELECT * FROM contract " +
                    "WHERE status = 'UNASSIGNED' " +
                    "AND region_id IN :idRegionList " +
                    "AND weight <= :weightContract",
            nativeQuery = true
    )
    List<Contract> getAllForCourier(
            List<Integer> idRegionList,
            float weightContract
    );

}
