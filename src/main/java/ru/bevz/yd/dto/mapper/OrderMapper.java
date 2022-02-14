package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.request.CompletedOrderRequest;
import ru.bevz.yd.controller.request.OrderInfo;
import ru.bevz.yd.controller.response.OrdersAssignOKResponse;
import ru.bevz.yd.controller.response.OrdersCompleteOKResponse;
import ru.bevz.yd.controller.response.OrdersCreatedResponse;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public ContractDTO toContractDTO(OrderInfo orderInfo) {
        return new ContractDTO()
                .setId(orderInfo.getId())
                .setWeight(orderInfo.getWeight())
                .setRegion(orderInfo.getRegion())
                .setTimePeriods(new HashSet<>(orderInfo.getDeliveryHours()));
    }

    public ContractDTO toContractDTO(CompletedOrderRequest completedOrderRequest) {
        return new ContractDTO()
                .setId(completedOrderRequest.getOrderId())
                .setCourierId(completedOrderRequest.getCourierId())
                .setDatetimeComplete(completedOrderRequest.getDateTimeCompleted());
    }

    public ContractDTO toContractDTO(Contract contract) {
        return new ContractDTO()
                .setId(contract.getId())
                .setRegion(contract.getRegion().getNumber())
                .setWeight(contract.getWeight())
                .setTimePeriods(
                        contract.getTimePeriods()
                                .stream()
                                .map(DateTimeUtils::toStringTP)
                                .collect(Collectors.toSet())
                );

    }

    public OrdersAssignOKResponse toOrdersAssignOKResponse(ContractDTO contractDTO) {
        return new OrdersAssignOKResponse().setIdOrders(
                        new IdList().setIdList(
                                contractDTO.getIdContracts()
                                        .stream()
                                        .map(id -> new Id().setId(id))
                                        .toList()
                        )
                )
                .setTimeAssigned(contractDTO.getDatetimeAssign());
    }

    public OrdersCompleteOKResponse toOrderCompleteOKResponse(ContractDTO contractDTO) {
        return new OrdersCompleteOKResponse()
                .setOrderId(contractDTO.getCourierId());
    }

    public OrdersCreatedResponse toOrdersCreatedResponse(ContractDTO contractDTO) {
        return new OrdersCreatedResponse().setOrders(
                new IdList().setIdList(
                        contractDTO.getIdContracts()
                                .stream()
                                .map(id -> new Id().setId(id))
                                .toList()
                )
        );
    }

}
