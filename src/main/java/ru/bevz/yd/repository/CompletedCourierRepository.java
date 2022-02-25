package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.CompletedCourier;

import java.util.Optional;

@Repository
public interface CompletedCourierRepository extends JpaRepository<CompletedCourier, Integer> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT *
                    FROM completed_courier AS ccour
                    WHERE ccour.courier_id = :courierId AND ccour.rate_id = :rateId AND ccour.ratio_id = :ratioId;
                    """
    )
    Optional<CompletedCourier> getCompletedCourierByOptions(int courierId, int rateId, int ratioId);
}
