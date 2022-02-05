package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.TypeCourier;

import java.util.Optional;

@Repository
public interface TypeCourierRepository extends JpaRepository<TypeCourier, Integer> {

    boolean existsByName(String name);

    TypeCourier getTypeCourierByName(String name);

    Optional<TypeCourier> findTypeCourierByName(String name);

}
