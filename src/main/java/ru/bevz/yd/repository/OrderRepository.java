package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Order;
import ru.bevz.yd.model.StatusOrder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Set<Order> getAllByCourierAndStatus(Courier courier, StatusOrder statusOrder);

    @Query(
            value = "SELECT DISTINCT ord.*\n" +
                    "FROM \"order\" ord\n" +
                    "         JOIN order_time_period ON ord.id = order_id\n" +
                    "         JOIN time_period ordtp ON ordtp.id = time_period_id\n" +
                    "         JOIN time_period courtp ON courtp.id IN :courierIdTimePeriodList\n" +
                    "WHERE ord.status = 'UNASSIGNED'\n" +
                    "  AND ord.region_id IN :courierIdRegionList\n" +
                    "  AND ord.weight <= :courierCapacity\n" +
                    "  AND (courtp.left_limit <= ordtp.left_limit AND ordtp.left_limit < courtp.right_limit\n" +
                    "    OR courtp.left_limit < ordtp.right_limit AND ordtp.right_limit <= courtp.right_limit\n" +
                    "    OR courtp.left_limit >= ordtp.left_limit AND courtp.right_limit <= ordtp.right_limit)",
            nativeQuery = true
    )
    Set<Order> getOrdersForAssigned(
            Set<Integer> courierIdRegionList,
            Set<Integer> courierIdTimePeriodList,
            float courierCapacity
    );

    @Query(
            value = "SELECT *\n" +
                    "FROM \"order\"\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'COMPLETED'\n" +
                    "  AND date(datetime_realization) = :dateRealization\n" +
                    "ORDER BY datetime_realization DESC\n" +
                    "LIMIT 1",
            nativeQuery = true
    )
    Optional<Order> getLastCompletedOrder(int courierId, LocalDate dateRealization);

    @Query(
            value = "SELECT *\n" +
                    "FROM \"order\"\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'ASSIGNED'\n" +
                    "  AND weight > :capacity",
            nativeQuery = true
    )
    Set<Order> getOrdersForRemoveByCapacity(int courierId, float capacity);


    @Query(
            value = "SELECT *\n" +
                    "FROM \"order\"\n" +
                    "WHERE courier_id = :courierId\n" +
                    "  AND status = 'ASSIGNED'\n" +
                    "  AND region_id NOT IN :regionIdList",
            nativeQuery = true
    )
    Set<Order> getOrdersForRemoveByRegion(int courierId, Set<Integer> regionIdList);

    @Query(
            value = "SELECT DISTINCT *\n" +
                    "FROM \"order\" ord\n" +
                    "         JOIN order_time_period ON ord.id = order_id\n" +
                    "         JOIN time_period ordtp ON time_period_id = ordtp.id\n" +
                    "         JOIN time_period courtp ON courtp.id IN :timePeriodIdList\n" +
                    "WHERE ord.courier_id = :courierId\n" +
                    "  AND ord.status = 'ASSIGNED'\n" +
                    "  AND NOT (courtp.left_limit <= ordtp.left_limit AND ordtp.left_limit < courtp.right_limit\n" +
                    "    OR courtp.left_limit < ordtp.right_limit AND ordtp.right_limit <= courtp.right_limit\n" +
                    "    OR courtp.left_limit >= ordtp.left_limit AND courtp.right_limit <= ordtp.right_limit)",
            nativeQuery = true
    )
    Set<Order> getOrdersForRemoveByTimePeriod(int courierId, Set<Integer> timePeriodIdList);

}