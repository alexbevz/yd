package ru.bevz.yd.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public float getEarningsCourier(int courierId) {
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        return courierRep.getEarningsByCourierIdAndAwardForContract(
                courierId,
                GlobalConstant.AWARD_FOR_CONTRACT
        ).orElse(0);
    }

    public float getRatingCourier(int courierId) {
        if (!courierRep.existsById(courierId)) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        int hs = 60 * 60;
        int t = courierRep.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId).orElse(hs);
        return (float) (hs - Math.min(t, hs)) / hs * 5;
    }

    @Transactional
    public CourierDTO addNewCouriers(List<CourierDTO> courierDTOs) {

        List<Integer> notValidCouriersId = new ArrayList<>();

        for (CourierDTO courierDto : courierDTOs) {
            try {
                addNewCourier(courierDto);
            } catch (Exception e) {
                notValidCouriersId.add(courierDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidCouriersId.isEmpty()) {
            throw new NotValidObjectsException("couriers", notValidCouriersId);
        }

        return new CourierDTO().setIdCouriers(
                courierDTOs
                        .stream()
                        .map(CourierDTO::getId)
                        .toList()
        );
    }

    @Transactional
    public CourierDTO addNewCourier(CourierDTO courierDTO) {
        int courierId = courierDTO.getId();
        String nameType = courierDTO.getType();

        Optional<Courier> optionalCourier = courierRep.findById(courierId);

        if (optionalCourier.isPresent()) {
            throw new EntityAlreadyExistsException(optionalCourier.get());
        }

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
    public CourierDTO patchCourier(CourierDTO courierDTO) {
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

    public CourierDTO getCourierInfoById(CourierDTO courierDTO) {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRep.findById(courierId);

        if (courierOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        courierDTO = courierMapper.toCourierDto(courierOptional.get());
        courierDTO.setRating(getRatingCourier(courierId));
        courierDTO.setEarnings(getEarningsCourier(courierId));

        return courierDTO;
    }

}
