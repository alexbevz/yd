package ru.bevz.yd.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bevz.yd.controller.request.CourierPatchRequest;
import ru.bevz.yd.controller.request.CouriersRequest;
import ru.bevz.yd.controller.response.CourierInfoResponse;
import ru.bevz.yd.controller.response.CouriersCreatedResponse;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.service.CourierService;

import java.util.List;

@RestController
@RequestMapping("/couriers")
@Tag(
        name = "Контроллер курьеров",
        description = "Обслуживает запросы связанные с курьерами"
)
public class CourierController {

    @Autowired
    private CourierService courierService;

    @Autowired
    private CourierMapper courierMapper;

    @PostMapping("")
    @Operation(
            summary = "Добавление курьеров",
            description = "Позволяет добавить новых курьеров в базу данных"
    )
    public ResponseEntity<Object> createCouriers(
            @RequestBody CouriersRequest couriersRequest
    ) {

        List<CourierDTO> courierDTOList = couriersRequest.getCourierInfos()
                .stream()
                .map(courierMapper::toCourierDto)
                .toList();

        CourierDTO courierDTO = courierService.createCouriers(courierDTOList);

        CouriersCreatedResponse response = courierMapper.toCouriersCreatedResponse(courierDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Изменение курьера",
            description = "Позволяет изменить характеристики курьера"
    )
    public ResponseEntity<Object> patchCourier(
            @PathVariable(value = "id") int courierId,
            @RequestBody CourierPatchRequest courierPatchRequest
    ) {

        CourierDTO courierDto = courierMapper.toCourierDto(courierPatchRequest)
                .setId(courierId);

        courierDto = courierService.patchCourier(courierDto);

        CourierInfoResponse response = courierMapper.toCourierInfoResponse(courierDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение курьера",
            description = "Позволяет получить полную информацию о курьере"
    )
    public ResponseEntity<Object> getCourier(
            @PathVariable(value = "id") int courierId
    ) {

        CourierDTO courierDTO = new CourierDTO().setId(courierId);

        courierDTO = courierService.getCourier(courierDTO);

        CourierInfoResponse response = courierMapper.toCourierInfoResponse(courierDTO);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
