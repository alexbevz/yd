package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.request.CourierPatchRequest;
import ru.bevz.yd.controller.response.CourierInfoResponse;
import ru.bevz.yd.controller.response.CouriersCreatedResponse;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class CourierMapper {

    public CourierDTO toCourierDto(CourierInfo courierInfo) {
        return new CourierDTO()
                .setId(courierInfo.getId())
                .setType(courierInfo.getCourierType())
                .setRegions(new HashSet<>(courierInfo.getRegions()))
                .setTimePeriods(new HashSet<>(courierInfo.getWorkingHours()));
    }

    public CourierDTO toCourierDto(CourierPatchRequest courierPatchRequest) {
        return new CourierDTO()
                .setType(courierPatchRequest.getCourierType())
                .setRegions(new HashSet<>(courierPatchRequest.getRegions()))
                .setTimePeriods(new HashSet<>(courierPatchRequest.getWorkingHours()));
    }

    public CourierDTO toCourierDto(Courier courier) {
        return new CourierDTO()
                .setId(courier.getId())
                .setType(courier.getTypeCourier().getName())
                .setRegions(
                        courier.getRegions()
                                .stream()
                                .map(Region::getNumber)
                                .collect(Collectors.toSet())
                )
                .setTimePeriods(
                        courier.getTimePeriods()
                                .stream()
                                .map(DateTimeUtils::toStringTP)
                                .collect(Collectors.toSet())
                );
    }

    public CourierInfoResponse toCourierInfoResponse(CourierDTO courierDTO) {
        return new CourierInfoResponse()
                .setId(courierDTO.getId())
                .setCourierType(courierDTO.getType())
                .setRegions(courierDTO.getRegions().stream().toList())
                .setWorkingHours(courierDTO.getTimePeriods().stream().toList())
                .setRating(courierDTO.getRating())
                .setEarnings(courierDTO.getEarnings());
    }

    public CouriersCreatedResponse toCouriersCreatedResponse(CourierDTO courierDTO) {
        return new CouriersCreatedResponse()
                .setCouriers(
                        new IdList()
                                .setIdList(
                                        courierDTO.getIdCouriers()
                                                .stream()
                                                .map(id -> new Id().setId(id))
                                                .toList()
                                )
                );
    }

}
