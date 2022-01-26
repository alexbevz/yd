package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Courier;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {

    @Query(
            value = "SELECT to_seconds(cast(MIN(subSelect.avgTimeRegions) AS time)) " +
                    "FROM (SELECT AVG(datetime_realization - datetime_realization_start) AS avgTimeRegions " +
                    "FROM contract " +
                    "WHERE courier_id = ?1 AND status = 'COMPLETED' " +
                    "GROUP BY region_id) AS subSelect ;"
            , nativeQuery = true
    )
    Optional<Integer> getMinAmongAvgTimeDeliveryRegionsByCourierId(int courierId);

    @Query(
            value = "SELECT SUM(?2 * profit_ratio) " +
                    "FROM contract " +
                    "JOIN type_courier ON type_courier.id = type_courier_id " +
                    "WHERE status = 'COMPLETED' AND courier_id = ?1 ;"
            , nativeQuery = true
    )
    Optional<Integer> getEarningsByCourierIdAndAwardForContract(int courierId, float award);

}
