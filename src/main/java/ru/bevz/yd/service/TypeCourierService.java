package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.repository.TypeCourierRepository;

@Service
public class TypeCourierService {

    @Autowired
    private TypeCourierRepository typeCourierRepository;

}
