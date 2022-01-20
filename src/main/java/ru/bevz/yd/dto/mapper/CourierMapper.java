package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.model.Courier;

@Component
public class CourierMapper {

    public CourierDto toCourierDto(CourierInfo courierInfo) {
        return new CourierDto();
    }

    public CourierDto toCourierDto(Courier courier) {
        return new CourierDto();
    }

    public CourierInfo toCourierInfo(CourierDto courierDto) {
        return new CourierInfo();
    }

}
