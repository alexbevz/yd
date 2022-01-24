package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.IdListCouriers;
import ru.bevz.yd.controller.response.CouriersBadRequestResponse;
import ru.bevz.yd.controller.response.CouriersCreatedResponse;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;

import java.util.stream.Collectors;

@Component
public class ValidAndNotValidIdListsMapper {

    public CouriersCreatedResponse toCouriersCreatedResponse(ValidAndNotValidIdLists valids) {
        return new CouriersCreatedResponse()
                .setCouriers(new IdListCouriers()
                        .setIdCouriers(new IdList()
                                .setIdList(valids.getValidIdList()
                                        .stream()
                                        .map(id -> new Id().setId(id))
                                        .collect(Collectors.toList()))));
    }

    public CouriersBadRequestResponse toCouriersBadRequestResponse(ValidAndNotValidIdLists valids) {
        return new CouriersBadRequestResponse()
                .setCouriers(new IdListCouriers()
                        .setIdCouriers(new IdList()
                                .setIdList(valids.getNotValidIdList()
                                        .stream()
                                        .map(id -> new Id().setId(id))
                                        .collect(Collectors.toList()))));
    }
}
