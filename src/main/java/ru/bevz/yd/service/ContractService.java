package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.repository.ContractRepository;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

}
