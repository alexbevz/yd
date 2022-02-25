package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Courier;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT dt_assigned
                    FROM assigned_courier
                    WHERE id = :courierId;
                    """
    )
    LocalDateTime getDTAssigned(int courierId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    INSERT INTO assigned_courier (id)
                    VALUES (:courierId);
                    """
    )
    void insertDTAssigned(int courierId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    DELETE FROM assigned_courier
                    WHERE id = :courierId;
                    """
    )
    void deleteDTAssigned(int courierId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT tcour.ratio_id
                    FROM courier AS cour
                             JOIN type_courier AS tcour
                                  ON cour.type_courier_id = tcour.id
                    WHERE cour.id = :courierId;
                    """
    )
    Optional<Integer> getRatioIdByCourierId(int courierId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT min(subSelect.avgTimeRegions)
                    FROM (SELECT avg(cord.dt_finish - cord.dt_start) AS avgTimeRegions
                          FROM "order" AS ord
                                   JOIN completed_order AS cord
                                        ON ord.id = cord.id
                                   JOIN completed_courier AS ccour
                                        ON cord.completed_courier_id = ccour.id
                          WHERE ccour.courier_id = :courierId
                          GROUP BY region_id) AS subSelect;
                    """
    )
    Optional<LocalDateTime> getMinAmongAvgTimeDeliveryRegionsByCourierId(int courierId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT SUM(ratio.value * rate.value)
                    FROM completed_courier AS ccour
                             JOIN completed_order AS cord
                                  ON ccour.id = cord.completed_courier_id
                             JOIN rate
                                  ON ccour.rate_id = rate.id
                             JOIN ratio
                                  ON ccour.ratio_id = ratio.id
                    WHERE ccour.courier_id = :courierId
                    """
    )
    Optional<Integer> getEarningsByCourierIdAndAwardForOrder(int courierId);

}
