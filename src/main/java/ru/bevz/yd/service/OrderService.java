package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.dto.mapper.OrderMapper;
import ru.bevz.yd.dto.model.OrderDTO;
import ru.bevz.yd.exception.*;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Order;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.TimePeriod;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.OrderRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private SecondaryService secondaryServ;

    @Autowired
    private OrderRepository orderRep;

    @Autowired
    private CourierRepository courierRep;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderDTO addOrders(Set<OrderDTO> orderDTOS) {

        Set<Integer> notValidOrdersId = new HashSet<>();

        for (OrderDTO orderDto : orderDTOS) {
            try {
                addOrder(orderDto);
            } catch (Exception e) {
                notValidOrdersId.add(orderDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidOrdersId.isEmpty()) {
            throw new NotValidObjectsException()
                    .setNameObjects("orders")
                    .setIdList(new IdList(notValidOrdersId));
        }

        return new OrderDTO().setIdOrders(
                orderDTOS
                        .stream()
                        .map(OrderDTO::getId)
                        .collect(Collectors.toSet())
        );
    }

    @Transactional
    public OrderDTO addOrder(OrderDTO orderDTO) {

        int orderId = orderDTO.getId();

        Optional<Order> optionalOrder = orderRep.findById(orderId);

        if (optionalOrder.isPresent()) {
            throw new EntityAlreadyExistsException(optionalOrder.get());
        }

        Order order = new Order();
        order.setId(orderId);
        order.setWeight(orderDTO.getWeight());
        order.setRegion(secondaryServ.getOrSaveRegionByNumber(orderDTO.getRegion()));
        order.setTimePeriods(secondaryServ.getOrSaveTimePeriodsByString(orderDTO.getTimePeriods()));

        return orderMapper.toOrderDTO(orderRep.save(order));
    }

    @Transactional
    public OrderDTO assignOrders(OrderDTO orderDTO) {
        int courierId = orderDTO.getCourierId();

        orderDTO = new OrderDTO();
        Optional<Courier> optionalCourier = courierRep.findById(courierId);

        if (optionalCourier.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        Courier courier = optionalCourier.get();

        Set<Integer> idOrders =
                orderRep.getIdAssignedOrdersByCourierId(courierId);

        if (!idOrders.isEmpty()) {
            orderDTO.setDatetimeAssign(courierRep.getDTAssigned(courierId).toString());
            orderDTO.setIdOrders(idOrders);
            return orderDTO;
        }

        float courierCapacity = courier.getTypeCourier().getCapacity();
        List<Order> orders = orderRep.getOrdersForAssignedByOptions(
                courier.getRegions()
                        .stream()
                        .map(Region::getId)
                        .collect(Collectors.toSet()),
                courier.getTimePeriods()
                        .stream()
                        .map(TimePeriod::getId)
                        .collect(Collectors.toSet()),
                courierCapacity
        );

        courierRep.deleteDTAssigned(courierId);

        if (orders.isEmpty()) {
            return orderDTO;
        }

        courierRep.insertDTAssigned(courierId);
        LocalDateTime DTAssignment = courierRep.getDTAssigned(courierId);

        float currentWeight = 0;

        for (Order order : orders) {
            float orderWeight = order.getWeight();
            int orderId = order.getId();
            if (currentWeight + orderWeight <= courierCapacity) {
                orderRep.insertAssignedOrder(orderId, courierId);
                orderRep.deleteUnassignedOrder(orderId);
                idOrders.add(orderId);
                currentWeight += orderWeight;
            } else {
                break;
            }
        }

        orderDTO.setDatetimeAssign(DTAssignment.toString());
        orderDTO.setIdOrders(idOrders);

        return orderDTO;
    }

    @Transactional
    public OrderDTO completeOrder(OrderDTO orderDTO) {
        int orderId = orderDTO.getId();
        int courierId = orderDTO.getCourierId();
        LocalDateTime dtFinish =
                LocalDateTime.parse(orderDTO.getDatetimeComplete());

        if (!orderRep.existsById(orderId)) {
            throw new EntityNotExistsException(new Order().setId(orderId));
        }

        //TODO: return other message
        if (orderRep.getIdCompletedOrderByOrderId(orderId).isPresent()) {
            throw new OrderHasBeenDeliveredException(new Order().setId(orderId));
        }

        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        //TODO: return other message
        if (orderRep.getCourierIdByCompletedOrderId(orderId) != courierId) {
            throw new OrderAssignedForOtherCourierException(new Order().setId(orderId));
        }

        //TODO: incorrect, need fix
        int completedCourierId = courierId;

        LocalDateTime dtStart =
                orderRep.getDTFinishForCompleting(courierId, dtFinish);

        orderRep.insertCompletedOrder(orderId, completedCourierId, dtStart, dtFinish);
        orderRep.deleteAssignedOrder(orderId);

        return orderDTO;
    }

}
