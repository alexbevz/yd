package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.StatusContract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> getAllByCourierAndStatus(Courier courier, StatusContract statusContract);

    @Query(
            value = "SELECT DISTINCT con.*\n" +
                    "FROM contract con\n" +
                    "         JOIN contract_time_period ON con.id = contract_id\n" +
                    "         JOIN time_period contp ON contp.id = time_period_id\n" +
                    "         JOIN time_period courtp ON courtp.id IN :courierIdTimePeriodList\n" +
                    "WHERE con.status = 'UNASSIGNED'\n" +
                    "  AND con.region_id IN :courierIdRegionList\n" +
                    "  AND con.weight <= :courierCapacity\n" +
                    "  AND (courtp.left_limit <= contp.left_limit AND contp.left_limit < courtp.right_limit\n" +
                    "    OR courtp.left_limit < contp.right_limit AND contp.right_limit <= courtp.right_limit\n" +
                    "    OR courtp.left_limit >= contp.left_limit AND courtp.right_limit <= contp.right_limit);",
            nativeQuery = true
    )
    List<Contract> getContractsForAssigned(
            List<Integer> courierIdRegionList,
            List<Integer> courierIdTimePeriodList,
            float courierCapacity
    );

    @Query(
            value = "SELECT *\n" +
                    "FROM contract\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'COMPLETED'\n" +
                    "  AND date(datetime_realization) = :dateRealization\n" +
                    "ORDER BY datetime_realization DESC\n" +
                    "LIMIT 1;",
            nativeQuery = true
    )
    Optional<Contract> getLastCompletedContract(int courierId, LocalDate dateRealization);

}
