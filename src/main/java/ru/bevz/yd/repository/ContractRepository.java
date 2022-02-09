package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.StatusContract;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    Set<Contract> getAllByCourierAndStatus(Courier courier, StatusContract statusContract);

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
                    "    OR courtp.left_limit >= contp.left_limit AND courtp.right_limit <= contp.right_limit)",
            nativeQuery = true
    )
    Set<Contract> getContractsForAssigned(
            Set<Integer> courierIdRegionList,
            Set<Integer> courierIdTimePeriodList,
            float courierCapacity
    );

    @Query(
            value = "SELECT *\n" +
                    "FROM contract\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'COMPLETED'\n" +
                    "  AND date(datetime_realization) = :dateRealization\n" +
                    "ORDER BY datetime_realization DESC\n" +
                    "LIMIT 1",
            nativeQuery = true
    )
    Optional<Contract> getLastCompletedContract(int courierId, LocalDate dateRealization);

    @Query(
            value = "SELECT *\n" +
                    "FROM contract\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'ASSIGNED'\n" +
                    "  AND weight > :capacity",
            nativeQuery = true
    )
    Set<Contract> getContractsForRemoveByCapacity(int courierId, float capacity);


    @Query(
            value = "SELECT *\n" +
                    "FROM contract\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'ASSIGNED'\n" +
                    "  AND region_id NOT IN :regionIdList",
            nativeQuery = true
    )
    Set<Contract> getContractsForRemoveByRegion(int courierId, Set<Integer> regionIdList);

    @Query(
            value = "SELECT DISTINCT *\n" +
                    "FROM contract con\n" +
                    "         JOIN contract_time_period ON con.id = contract_id\n" +
                    "         JOIN time_period contp ON time_period_id = contp.id\n" +
                    "         JOIN time_period courtp ON courtp.id IN :timePeriodIdList\n" +
                    "WHERE con.courier_id = :courierId\n" +
                    "  AND con.status = 'ASSIGNED'\n" +
                    "  AND NOT (courtp.left_limit <= contp.left_limit AND contp.left_limit < courtp.right_limit\n" +
                    "    OR courtp.left_limit < contp.right_limit AND contp.right_limit <= courtp.right_limit\n" +
                    "    OR courtp.left_limit >= contp.left_limit AND courtp.right_limit <= contp.right_limit)",
            nativeQuery = true
    )
    Set<Contract> getContractsForRemoveByTimePeriod(int courierId, Set<Integer> timePeriodIdList);

}
