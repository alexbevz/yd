package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.repository.CourierRepository;

import java.util.List;

@Service
public class CourierService {

    @Autowired
    private CourierRepository courierRepository;

    public void addNewCouriers(List<CourierDto> courierDtoList) {

    }

    public CourierDto patchCourier(CourierDto courierDto) {
        return new CourierDto();
    }

    public CourierDto getCourierInfo(CourierDto courierDto) {
        return new CourierDto();
    }

}
