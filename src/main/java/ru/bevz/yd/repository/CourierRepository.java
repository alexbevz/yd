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
            value = "SELECT to_seconds(cast(MIN(subSelect.avgTimeRegions) AS time)) " +
                    "FROM (SELECT AVG(datetime_realization - datetime_realization_start) AS avgTimeRegions " +
                    "FROM \"order\" " +
                    "WHERE courier_id = :courierId AND status = 'COMPLETED' " +
                    "GROUP BY region_id) AS subSelect ;"
            , nativeQuery = true
    )
    Optional<Integer> getMinAmongAvgTimeDeliveryRegionsByCourierId(int courierId);

    @Query(
            value = "SELECT SUM(:award * profit_ratio) " +
                    "FROM \"order\" " +
                    "JOIN type_courier ON type_courier.id = type_courier_id " +
                    "WHERE status = 'COMPLETED' AND courier_id = :courierId ;"
            , nativeQuery = true
    )
    Optional<Integer> getEarningsByCourierIdAndAwardForOrder(int courierId, float award);

}
