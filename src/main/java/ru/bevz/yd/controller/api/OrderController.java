package ru.bevz.yd.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bevz.yd.controller.request.CompletedOrderRequest;
import ru.bevz.yd.controller.request.OrdersRequest;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.response.OrdersAssignOKResponse;
import ru.bevz.yd.controller.response.OrdersCompleteOKResponse;
import ru.bevz.yd.controller.response.OrdersCreatedResponse;
import ru.bevz.yd.dto.mapper.OrderMapper;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(
        name = "Контроллер заказов",
        description = "Обслуживает запросы связанные с заказами"
)
public class OrderController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("")
    @Operation(
            summary = "Добавление заказов",
            description = "Позволяет добавить новые заказы в базу данных"
    )
    @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = OrdersCreatedResponse.class))
    )
    public ResponseEntity<Object> createOrders(
            @RequestBody OrdersRequest ordersRequest
    ) {

        List<ContractDTO> contractDTOs = ordersRequest.getOrderInfos()
                .stream()
                .map(orderMapper::toContractDTO)
                .toList();

        ContractDTO courierDto = contractService.addContracts(contractDTOs);

        OrdersCreatedResponse response = orderMapper.toOrdersCreatedResponse(courierDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/assign")
    @Operation(
            summary = "Назначение заказов",
            description = "Позволяет назначить заказы выбранному курьеру по его характеристиками"
    )
    public ResponseEntity<Object> assignOrders(
            @RequestBody CourierInfo courierInfo
    ) {

        ContractDTO contractDto = new ContractDTO().setCourierId(courierInfo.getId());

        contractDto = contractService.assignContracts(contractDto);

        OrdersAssignOKResponse response = orderMapper.toOrdersAssignOKResponse(contractDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/complete")
    @Operation(
            summary = "Выполнение заказа",
            description = "Позволяет отметить заказ выполненным для опредленного курьера"
    )
    public ResponseEntity<Object> completeOrder(
            @RequestBody CompletedOrderRequest completedOrderRequest
    ) {

        ContractDTO contractDto = orderMapper.toContractDTO(completedOrderRequest);

        contractDto = contractService.completeContract(contractDto);

        OrdersCompleteOKResponse response = orderMapper.toOrderCompleteOKResponse(contractDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
