package ru.bevz.yd.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.request.CouriersRequest;
import ru.bevz.yd.controller.response.CouriersBadRequestResponse;
import ru.bevz.yd.controller.response.CouriersCreatedResponse;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.mapper.ValidAndNotValidIdListsMapper;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.service.CourierService;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @Autowired
    private CourierMapper courierMapper;

    @Autowired
    private ValidAndNotValidIdListsMapper validMapper;

    @PostMapping("")
    public ResponseEntity<Object> createCouriers(@RequestBody CouriersRequest couriersRequest) {
        List<CourierDto> courierDtoList = couriersRequest.getCourierInfoList()
                .stream()
                .map(courierInfo -> courierMapper.toCourierDto(courierInfo))
                .toList();

        ValidAndNotValidIdLists valid = courierService.addNewCouriers(courierDtoList);

        if (valid.hasNotValid()) {
            CouriersBadRequestResponse response = validMapper.toCouriersBadRequestResponse(valid);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        CouriersCreatedResponse response = validMapper.toCouriersCreatedResponse(valid);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchCourier(
            @PathVariable(value = "id") int courierId
            , @RequestBody CourierInfo courierInfo
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCourier(@PathVariable(value = "id") int courierId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
