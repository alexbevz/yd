package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.request.CourierPatchRequest;
import ru.bevz.yd.controller.response.CourierIdOKResponse;
import ru.bevz.yd.controller.response.CourierInfoResponse;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.TypeCourier;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.stream.Collectors;

@Component
public class CourierMapper {

    public CourierDto toCourierDto(CourierInfo courierInfo) {
        return new CourierDto()
                .setId(courierInfo.getId())
                .setType(courierInfo.getCourierType())
                .setRegionList(courierInfo.getRegions())
                .setTimePeriodList(courierInfo.getWorkingHours());
    }

    public CourierDto toCourierDto(CourierPatchRequest courierPatchRequest) {
        return new CourierDto()
                .setType(courierPatchRequest.getCourierType())
                .setRegionList(courierPatchRequest.getRegions())
                .setTimePeriodList(courierPatchRequest.getWorkingHours());
    }

    public CourierDto toCourierDto(Courier courier) {
        return new CourierDto()
                .setId(courier.getId())
                .setType(courier.getTypeCourier().getName())
                .setRegionList(courier.getRegionList()
                        .stream()
                        .map(Region::getNumberRegion)
                        .collect(Collectors.toList()))
                .setTimePeriodList(courier.getTimePeriodList()
                        .stream()
                        .map(DateTimeUtils::toStringTP)
                        .collect(Collectors.toList()));
    }

    public CourierInfo toCourierInfo(CourierDto courierDto) {
        return new CourierInfo()
                .setId(courierDto.getId())
                .setCourierType(courierDto.getType())
                .setRegions(courierDto.getRegionList())
                .setWorkingHours(courierDto.getTimePeriodList());
    }

    public CourierInfoResponse toCourierInfoResponse(CourierDto courierDto) {
        return new CourierInfoResponse()
                .setId(courierDto.getId())
                .setCourierType(courierDto.getType())
                .setRegions(courierDto.getRegionList())
                .setWorkingHours(courierDto.getTimePeriodList())
                .setRating(courierDto.getRating())
                .setEarnings(courierDto.getEarnings());
    }

    public CourierIdOKResponse toCourierIdOkResponse(CourierDto courierDto) {
        return new CourierIdOKResponse()
                .setId(courierDto.getId())
                .setCourierType(courierDto.getType())
                .setRegions(courierDto.getRegionList())
                .setWorkingHours(courierDto.getTimePeriodList());
    }

    public Courier toCourier(CourierDto courierDto) {
        return new Courier()
                .setId(courierDto.getId())
                .setTypeCourier(new TypeCourier().setName(courierDto.getType()))
                .setRegionList(courierDto.getRegionList()
                        .stream()
                        .map(num -> new Region().setNumberRegion(num))
                        .collect(Collectors.toSet()))
                .setTimePeriodList(courierDto.getTimePeriodList()
                        .stream()
                        .map(DateTimeUtils::toTimePeriod)
                        .collect(Collectors.toSet()));
    }

}
