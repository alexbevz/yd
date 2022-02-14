package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.dto.mapper.OrderMapper;
import ru.bevz.yd.dto.model.OrderDTO;
import ru.bevz.yd.exception.*;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.OrderRepository;
import ru.bevz.yd.repository.CourierRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bevz.yd.util.DateTimeUtils.findTPForTime;
import static ru.bevz.yd.util.DateTimeUtils.isTimeInTP;

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
    public OrderDTO addOrders(List<OrderDTO> orderDTOS) {

        List<Integer> notValidOrdersId = new ArrayList<>();

        for (OrderDTO orderDto : orderDTOS) {
            try {
                addOrder(orderDto);
            } catch (Exception e) {
                notValidOrdersId.add(orderDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidOrdersId.isEmpty()) {
            throw new NotValidObjectsException("orders", notValidOrdersId);
        }

        return new OrderDTO().setIdOrders(
                orderDTOS
                        .stream()
                        .map(OrderDTO::getId)
                        .toList()
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
        order.setStatus(StatusOrder.UNASSIGNED);

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

        Set<Order> orders =
                orderRep.getAllByCourierAndStatus(courier, StatusOrder.ASSIGNED);

        if (!orders.isEmpty()) {
            orderDTO.setDatetimeAssign(
                    orders
                            .stream()
                            .findAny()
                            .get()
                            .getDatetimeAssignment()
                            .toString()
            );
            orderDTO.setIdOrders(orders.stream().map(Order::getId).toList());
            return orderDTO;
        }

        orders = orderRep.getOrdersForAssigned(
                courier.getRegions()
                        .stream()
                        .map(Region::getId)
                        .collect(Collectors.toSet()),
                courier.getTimePeriods()
                        .stream()
                        .map(TimePeriod::getId)
                        .collect(Collectors.toSet()),
                courier.getTypeCourier().getCapacity()
        );

        if (orders.isEmpty()) {
            return orderDTO;
        }

        LocalDateTime dateTimeAssignment = LocalDateTime.now();

        for (Order order : orders) {
            order.setDatetimeAssignment(dateTimeAssignment);
            order.setStatus(StatusOrder.ASSIGNED);
            order.setCourier(courier);
        }

        orderDTO.setDatetimeAssign(dateTimeAssignment.toString());
        orderDTO.setIdOrders(
                orders
                        .stream()
                        .map(Order::getId)
                        .toList()
        );

        return orderDTO;
    }

    @Transactional
    public OrderDTO completeOrder(OrderDTO orderDTO) {
        int orderId = orderDTO.getId();
        int courierId = orderDTO.getCourierId();
        LocalDateTime DTCompleted =
                LocalDateTime.parse(orderDTO.getDatetimeComplete());

        Optional<Order> orderOptional = orderRep.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new EntityNotExistsException(new Order().setId(orderId));
        }
        Order order = orderOptional.get();
        if (order.getStatus() == StatusOrder.COMPLETED) {
            throw new OrderHasBeenDeliveredException(order);
        }

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        Courier courier = courierOptional.get();

        if (order.getCourier().getId() != courierId) {
            throw new OrderAssignedForOtherCourierException(order);
        }

        Optional<TimePeriod> courierTimePeriodOptional =
                findTPForTime(courier.getTimePeriods(), DTCompleted);
        if (courierTimePeriodOptional.isEmpty()) {
            throw new NotFoundTPForEntityException(courier);
        }
        TimePeriod courierTP = courierTimePeriodOptional.get();

        Optional<Order> lastOrderOptional =
                orderRep.getLastCompletedOrder(courierId, DTCompleted.toLocalDate());

        LocalDateTime dateTimeRealizationStart;

        if (lastOrderOptional.isPresent()) {
            Order lastOrder = lastOrderOptional.get();
            if (isTimeInTP(courierTP, lastOrder.getDatetimeRealization())) {
                dateTimeRealizationStart = lastOrder.getDatetimeRealization();
            } else {
                dateTimeRealizationStart =
                        LocalDateTime.of(DTCompleted.toLocalDate(), courierTP.getFrom());
            }
        } else {
            dateTimeRealizationStart =
                    LocalDateTime.of(DTCompleted.toLocalDate(), courierTP.getFrom());
        }

        order.setStatus(StatusOrder.COMPLETED);
        order.setDatetimeRealizationStart(dateTimeRealizationStart);
        order.setDatetimeRealization(DTCompleted);
        order.setTypeCourier(courier.getTypeCourier());

        return orderDTO;
    }

}
