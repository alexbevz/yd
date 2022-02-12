package ru.bevz.yd.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.constant.GlobalConstant;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.exception.EntityAlreadyExistsException;
import ru.bevz.yd.exception.EntityNotExistsException;
import ru.bevz.yd.exception.NotValidObjectsException;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.TypeCourierRepository;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
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
    private ContractRepository contractRep;

    @Autowired
    private CourierMapper courierMapper;

    public float getCourierEarnings(int courierId) {
        //TODO: Implement like wrapper
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        return courierRep.getEarningsByCourierIdAndAwardForContract(
                courierId,
                GlobalConstant.AWARD_FOR_CONTRACT
        ).orElse(0);
    }

    public float getCourierRating(int courierId) {
        //TODO: Implement like wrapper
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        int hs = 60 * 60;
        int t = courierRep.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId).orElse(hs);
        return (float) (hs - Math.min(t, hs)) / hs * 5;
    }

    @Transactional
    public CourierDTO createCouriers(@NotNull List<CourierDTO> courierDTOs) {
        if (courierDTOs.isEmpty()) {
            throw new NullPointerException("Empty data!");
        }

        List<Integer> notValidCouriersId = new ArrayList<>();
        for (CourierDTO courierDto : courierDTOs) {
            try {
                createCourier(courierDto);
            } catch (Exception e) {
                notValidCouriersId.add(courierDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidCouriersId.isEmpty()) {
            throw new NotValidObjectsException("couriers", notValidCouriersId);
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

        String newTypeCourierStr = courierDTO.getType();
        if (newTypeCourierStr != null) {
            Optional<TypeCourier> newTypeCourierOptional =
                    typeCourierRep.findTypeCourierByName(newTypeCourierStr);
            if (newTypeCourierOptional.isEmpty()) {
                throw new EntityNotExistsException(newTypeCourierStr);
            }
            TypeCourier newTypeCourier = newTypeCourierOptional.get();

            if (originalCourier.getTypeCourier().getCapacity() > newTypeCourier.getCapacity()) {
                Set<Contract> contractsForRemove =
                        contractRep.getContractsForRemoveByCapacity(courierId, newTypeCourier.getCapacity());
                for (Contract contract : contractsForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
            }
            originalCourier.setTypeCourier(newTypeCourier);
        }

        Set<Integer> newRegionListStr = courierDTO.getRegions();
        if (newRegionListStr != null) {
            Set<Region> newRegions =
                    secondaryServ.getOrSaveRegionsByNumber(courierDTO.getRegions());

            if (!originalCourier.getRegions().containsAll(newRegions)) {
                Set<Contract> contractListForRemove =
                        contractRep.getContractsForRemoveByRegion(
                                courierId,
                                newRegions
                                        .stream()
                                        .map(Region::getId)
                                        .collect(Collectors.toSet())
                        );
                for (Contract contract : contractListForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
                originalCourier.setRegions(newRegions);
            }
        }

        Set<String> newTimePeriodListStr = courierDTO.getTimePeriods();
        if (newTimePeriodListStr != null) {
            Set<TimePeriod> newTimePeriods =
                    secondaryServ.getOrSaveTimePeriodsByString(courierDTO.getTimePeriods());

            if (!originalCourier.getTimePeriods().containsAll(newTimePeriods)) {
                Set<Contract> contractsForRemove =
                        contractRep.getContractsForRemoveByTimePeriod(
                                courierId,
                                newTimePeriods
                                        .stream()
                                        .map(TimePeriod::getId)
                                        .collect(Collectors.toSet())
                        );
                for (Contract contract : contractsForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
                originalCourier.setTimePeriods(newTimePeriods);
            }
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
