package ru.bevz.yd.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.exception.EntityAlreadyExistsException;
import ru.bevz.yd.exception.EntityNotExistsException;
import ru.bevz.yd.exception.NotValidObjectsException;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.OrderRepository;
import ru.bevz.yd.repository.TypeCourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CourierService {

    @Autowired
    private SecondaryService secondaryServ;

    @Autowired
    private CourierRepository courierRep;

    @Autowired
    private TypeCourierRepository typeCourierRep;

    @Autowired
    private OrderRepository orderRep;

    @Autowired
    private CourierMapper courierMapper;

    public float getCourierEarnings(int courierId) {
        //TODO: Implement like wrapper
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        return courierRep.getEarningsByCourierIdAndAwardForOrder(
                courierId
        ).orElse(0);
    }

    public float getCourierRating(int courierId) {
        //TODO: Implement like wrapper
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        int m = 60;
        int hs = m * 60;
        Optional<LocalDateTime> dtOptional =
                courierRep.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId);
        int t = hs;
        if (dtOptional.isPresent()) {
            LocalDateTime dt = dtOptional.get();
            t = dt.getHour() * hs + dt.getMinute() * m + dt.getSecond();
        }
        return (float) (hs - Math.min(t, hs)) / hs * 5;
    }

    public int getCurrentCompletedCourier(int courierId) {
        //TODO: Implement like wrapper
        if (courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        Optional<Integer> ratioIdOptional = courierRep.getRatioIdByCourierId(courierId);
        if (ratioIdOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        return secondaryServ.getOrSaveCompletedCourier(courierId, ratioIdOptional.get()).getId();
    }

    @Transactional
    public CourierDTO createCouriers(@NotNull Set<CourierDTO> courierDTOs) {
        if (courierDTOs.isEmpty()) {
            throw new NullPointerException("Empty data!");
        }

        Set<Integer> notValidCouriersId = new HashSet<>();
        for (CourierDTO courierDto : courierDTOs) {
            try {
                createCourier(courierDto);
            } catch (Exception e) {
                notValidCouriersId.add(courierDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidCouriersId.isEmpty()) {
            throw new NotValidObjectsException()
                    .setNameObjects("couriers")
                    .setIdList(new IdList(notValidCouriersId));
        }

        return courierMapper.toCourierDTO(courierDTOs);
    }

    @Transactional
    public CourierDTO createCourier(@NotNull CourierDTO courierDTO) {
        //TODO: Implement like wrapper
        if (!validateCourierDTO(courierDTO)) {
            throw new ValidationException(courierDTO.toString());
        }

        int courierId = courierDTO.getId();

        Optional<Courier> optionalCourier = courierRep.findById(courierId);
        if (optionalCourier.isPresent()) {
            throw new EntityAlreadyExistsException(optionalCourier.get());
        }

        String nameType = courierDTO.getType();
        Optional<TypeCourier> typeCourierOptional =
                typeCourierRep.findTypeCourierByName(nameType);
        if (typeCourierOptional.isEmpty()) {
            throw new EntityNotExistsException(new TypeCourier().setName(nameType));
        }

        Courier courier = new Courier();
        courier.setId(courierId);

        TypeCourier typeCourier = typeCourierOptional.get();
        courier.setTypeCourier(typeCourier);

        Set<Region> regions =
                secondaryServ.getOrSaveRegionsByNumber(courierDTO.getRegions());
        courier.setRegions(regions);

        Set<TimePeriod> timePeriods =
                secondaryServ.getOrSaveTimePeriodsByString(courierDTO.getTimePeriods());

        courier.setTimePeriods(timePeriods);

        return courierMapper.toCourierDto(courierRep.save(courier));
    }

    @Transactional
    public CourierDTO patchCourier(@NotNull CourierDTO courierDTO) {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        Courier originalCourier = courierOptional.get();

        Set<Order> assignedOrders =
                orderRep.getAssignedOrderByCourierId(courierId);

        Set<Integer> newRegionListStr = courierDTO.getRegions();
        if (newRegionListStr != null) {
            Set<Region> newRegions =
                    secondaryServ.getOrSaveRegionsByNumber(courierDTO.getRegions());

            if (!originalCourier.getRegions().containsAll(newRegions)) {
                for (Order order : assignedOrders) {
                    if (!newRegions.contains(order.getRegion())) {
                        int orderId = order.getId();
                        assignedOrders.remove(order);
                        orderRep.createUnassignedOrder(orderId);
                        orderRep.deleteAssignedOrder(orderId);
                    }
                }
                originalCourier.setRegions(newRegions);
            }
        }

        Set<String> newTimePeriodListStr = courierDTO.getTimePeriods();
        if (newTimePeriodListStr != null) {
            Set<TimePeriod> newTimePeriods =
                    secondaryServ.getOrSaveTimePeriodsByString(courierDTO.getTimePeriods());

            if (!originalCourier.getTimePeriods().containsAll(newTimePeriods)) {
                for (Order order : assignedOrders) {
                    for (TimePeriod tp : order.getTimePeriods()) {
                        if (
                                newTimePeriods.stream().anyMatch(
                                        val -> DateTimeUtils.isTimeInTP(val, tp.getFrom()) ||
                                                DateTimeUtils.isTimeInTP(val, tp.getTo()))
                        ) {
                            int orderId = order.getId();
                            assignedOrders.remove(order);
                            orderRep.createUnassignedOrder(orderId);
                            orderRep.deleteAssignedOrder(orderId);
                        }
                    }
                }
                originalCourier.setTimePeriods(newTimePeriods);
            }
        }

        String newTypeCourierStr = courierDTO.getType();
        if (newTypeCourierStr != null) {
            Optional<TypeCourier> newTypeCourierOptional =
                    typeCourierRep.findTypeCourierByName(newTypeCourierStr);
            if (newTypeCourierOptional.isEmpty()) {
                throw new EntityNotExistsException(newTypeCourierStr);
            }
            TypeCourier newTypeCourier = newTypeCourierOptional.get();
            float newCapacity = newTypeCourier.getCapacity();

            float sum = 0;
            for (float weight : assignedOrders.stream().map(Order::getWeight).collect(Collectors.toSet())) {
                sum += weight;
            }

            if (originalCourier.getTypeCourier().getCapacity() > newCapacity) {
                for (Order order : assignedOrders.stream()
                        .sorted(Comparator.comparingDouble(Order::getWeight).reversed())
                        .toList()
                ) {
                    if (sum > newCapacity) {
                        int orderId = order.getId();
                        assignedOrders.remove(order);
                        orderRep.createUnassignedOrder(orderId);
                        orderRep.deleteAssignedOrder(orderId);
                    }
                }
            }
            originalCourier.setTypeCourier(newTypeCourier);
        }

        courierDTO = courierMapper.toCourierDto(originalCourier);
        return courierDTO;
    }

    public CourierDTO getCourier(@NotNull CourierDTO courierDTO) {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        courierDTO = courierMapper.toCourierDto(courierOptional.get());
        courierDTO.setRating(getCourierRating(courierId));
        courierDTO.setEarnings(getCourierEarnings(courierId));

        return courierDTO;
    }

    //TODO: Add the validation aspect class for courier
    public boolean validateCourierDTO(@NotNull CourierDTO courierDTO) {
        int courierId = courierDTO.getId();
        String nameType = courierDTO.getType();
        Set<Integer> regionNumbers = courierDTO.getRegions();
        Set<String> timePeriodsStr = courierDTO.getTimePeriods();

        return courierId > 0 &&
                nameType != null &&
                !regionNumbers.isEmpty() &&
                regionNumbers.stream().allMatch(num -> num > 0) &&
                !timePeriodsStr.isEmpty() &&
                timePeriodsStr
                        .stream()
                        .allMatch(str -> Pattern.matches(
                                "^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})-(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$",
                                str));
    }

}
