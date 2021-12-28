package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.repository.CourierRepository;

@Service
public class CourierService {

    @Autowired
    private CourierRepository courierRepository;

}
