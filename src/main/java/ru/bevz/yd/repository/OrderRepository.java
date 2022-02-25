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
                    INSERT INTO unassigned_order
                    VALUES (:orderId);
                    """
    )
    void createUnassignedOrder(int orderId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    DELETE FROM unassigned_order
                    WHERE id = :orderId ;
                    """
    )
    void deleteUnassignedOrder(int orderId);

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
                    SELECT coalesce(max(cord.dt_finish), :dtAssigned)
                    FROM completed_courier AS ccour
                             JOIN completed_order AS cord
                                  ON ccour.id = cord.completed_courier_id AND cord.dt_finish > :dtAssigned
                    WHERE ccour.courier_id = :courierId;
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
            nativeQuery = true,
            value = """
                    SELECT DISTINCT ord.*
                    FROM "order" AS ord
                             JOIN assigned_order AS aord
                                  ON aord.assigned_courier_id = :courierId AND ord.id = aord.id;
                    """
    )
    Set<Order> getAssignedOrderByCourierId(int courierId);

}
