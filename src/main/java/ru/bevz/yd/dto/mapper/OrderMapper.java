package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.request.CompletedOrderRequest;
import ru.bevz.yd.controller.request.OrderInfo;
import ru.bevz.yd.controller.response.OrdersAssignOKResponse;
import ru.bevz.yd.controller.response.OrdersCompleteOKResponse;
import ru.bevz.yd.controller.response.OrdersCreatedResponse;
import ru.bevz.yd.dto.model.OrderDTO;
import ru.bevz.yd.model.Order;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDTO toOrderDTO(OrderInfo orderInfo) {
        return new OrderDTO()
                .setId(orderInfo.getId())
                .setWeight(orderInfo.getWeight())
                .setRegion(orderInfo.getRegion())
                .setTimePeriods(new HashSet<>(orderInfo.getDeliveryHours()));
    }

    public OrderDTO toOrderDTO(CompletedOrderRequest completedOrderRequest) {
        return new OrderDTO()
                .setId(completedOrderRequest.getOrderId())
                .setCourierId(completedOrderRequest.getCourierId())
                .setDatetimeComplete(completedOrderRequest.getDateTimeCompleted());
    }

    public OrderDTO toOrderDTO(Order order) {
        return new OrderDTO()
                .setId(order.getId())
                .setRegion(order.getRegion().getNumber())
                .setWeight(order.getWeight())
                .setTimePeriods(
                        order.getTimePeriods()
                                .stream()
                                .map(DateTimeUtils::toStringTP)
                                .collect(Collectors.toSet())
                );

    }

    public OrdersAssignOKResponse toOrdersAssignOKResponse(OrderDTO orderDTO) {
        return new OrdersAssignOKResponse().setIdOrders(
                        new IdList().setIdList(
                                orderDTO.getIdOrders()
                                        .stream()
                                        .map(id -> new Id().setId(id))
                                        .collect(Collectors.toSet())
                        )
                )
                .setTimeAssigned(orderDTO.getDatetimeAssign());
    }

    public OrdersCompleteOKResponse toOrderCompleteOKResponse(OrderDTO orderDTO) {
        return new OrdersCompleteOKResponse()
                .setOrderId(orderDTO.getCourierId());
    }

    public OrdersCreatedResponse toOrdersCreatedResponse(OrderDTO orderDTO) {
        return new OrdersCreatedResponse().setOrders(
                new IdList().setIdList(
                        orderDTO.getIdOrders()
                                .stream()
                                .map(id -> new Id().setId(id))
                                .collect(Collectors.toSet())
                )
        );
    }

}
