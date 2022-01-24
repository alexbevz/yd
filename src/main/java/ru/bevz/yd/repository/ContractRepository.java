package ru.bevz.yd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bevz.yd.model.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

}
