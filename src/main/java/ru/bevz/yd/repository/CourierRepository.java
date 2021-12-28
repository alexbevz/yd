package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Courier;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

}
