package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    DELETE FROM unassigned_order
                    WHERE id = :orderId;
                    """
    )
    void deleteUnassignedOrder(int courierId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    INSERT INTO assigned_order (id, assigned_courier_id)
                    VALUES (:orderId, :courierId);
                    """
    )
    void insertAssignedOrder(int orderId, int courierId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    DELETE FROM assigned_order
                    WHERE id = :orderId;
                    """
    )
    void deleteAssignedOrder(int orderId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    INSERT INTO completed_order
                    VALUES (:orderId, :courierCompletedId, :dtStart, :dtFinish);
                    """
    )
    void insertCompletedOrder(
            int orderId,
            int courierCompletedId,
            LocalDateTime dtStart,
            LocalDateTime dtFinish
    );

    @Query(
            nativeQuery = true,
            value = """
                    SELECT aord.id
                    FROM assigned_order AS aord
                    WHERE aord.assigned_courier_id = :courierId;
                    """
    )
    Set<Integer> getIdAssignedOrdersByCourierId(int courierId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT *
                    FROM "order" AS ord
                             JOIN order_time_period
                                  ON ord.id = order_id
                             JOIN time_period AS ordtp
                                  ON ordtp.id = time_period_id
                             JOIN time_period AS courtp
                                  ON courtp.id IN :courierIdTimePeriodList
                    WHERE ord.region_id IN :courierIdRegionList
                      AND ord.weight <= :courierCapacity
                      AND (courtp.left_limit <= ordtp.left_limit AND ordtp.left_limit < courtp.right_limit
                        OR courtp.left_limit < ordtp.right_limit AND ordtp.right_limit <= courtp.right_limit
                        OR courtp.left_limit >= ordtp.left_limit AND courtp.right_limit <= ordtp.right_limit)
                    ORDER BY weight;
                    """
    )
    List<Order> getOrdersForAssignedByOptions(
            Set<Integer> courierIdRegionList,
            Set<Integer> courierIdTimePeriodList,
            float courierCapacity
    );

    @Query(
            nativeQuery = true,
            value = """
                    SELECT cord.id
                    FROM completed_order AS cord
                    WHERE cord.id = :orderId;
                    """
    )
    Optional<Integer> getIdCompletedOrderByOrderId(int orderId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT coalesce(MAX(cord.dt_finish), :dtAssigned)
                    FROM courier AS cour
                             JOIN completed_courier AS ccour
                                  ON cour.id = ccour.courier_id
                             JOIN completed_order AS cord
                                  ON ccour.id = cord.completed_courier_id AND cord.dt_finish > :dtAssigned;
                    """
    )
    LocalDateTime getDTFinishForCompleting(int courierId, LocalDateTime dtAssigned);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT aord.assigned_courier_id
                    FROM assigned_order AS aord
                    WHERE aord.id = :orderId;
                    """
    )
    Integer getCourierIdByCompletedOrderId(int orderId);

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
