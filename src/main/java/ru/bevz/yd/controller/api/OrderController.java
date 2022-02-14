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
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.request.OrdersRequest;
import ru.bevz.yd.controller.response.OrdersAssignOKResponse;
import ru.bevz.yd.controller.response.OrdersCompleteOKResponse;
import ru.bevz.yd.controller.response.OrdersCreatedResponse;
import ru.bevz.yd.dto.mapper.OrderMapper;
import ru.bevz.yd.dto.model.OrderDTO;
import ru.bevz.yd.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(
        name = "Контроллер заказов",
        description = "Обслуживает запросы связанные с заказами"
)
public class OrderController {

    @Autowired
    private OrderService orderService;

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

        List<OrderDTO> orderDTOS = ordersRequest.getOrderInfos()
                .stream()
                .map(orderMapper::toOrderDTO)
                .toList();

        OrderDTO courierDto = orderService.addOrders(orderDTOS);

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

        OrderDTO orderDto = new OrderDTO().setCourierId(courierInfo.getId());

        orderDto = orderService.assignOrders(orderDto);

        OrdersAssignOKResponse response = orderMapper.toOrdersAssignOKResponse(orderDto);

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

        OrderDTO orderDto = orderMapper.toOrderDTO(completedOrderRequest);

        orderDto = orderService.completeOrder(orderDto);

        OrdersCompleteOKResponse response = orderMapper.toOrderCompleteOKResponse(orderDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
