package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.constants.GlobalConstant;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.TypeCourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourierService {

    @Autowired
    protected ContractRepository contractRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private TypeCourierRepository typeCourierRepository;
    @Autowired
    private RegionService regionService;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private CourierMapper courierMapper;

    @Transactional
    public ValidAndNotValidIdLists addNewCouriers(List<CourierDto> courierDtoList) {
        ValidAndNotValidIdLists valid = new ValidAndNotValidIdLists();

        for (CourierDto courierDto : courierDtoList) {
            try {
                Courier courier = addNewCourier(courierDto);
                valid.addValidId(courier.getId());
            } catch (Exception e) {
                valid.addNotValidId(courierDto.getId());
            }
        }

        if (valid.hasNotValid()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return valid;
    }

    @Transactional
    public CourierDto patchCourier(CourierDto courierDto) throws Exception {
        int courierId = courierDto.getId();

        Optional<Courier> courierOptional = courierRepository.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new Exception("Courier with id " + courierId + "do not exist!");
        }
        Courier originalCourier = courierOptional.get();

        String newTypeCourierStr = courierDto.getType();
        if (newTypeCourierStr != null) {
            Optional<TypeCourier> newTypeCourierOptional =
                    typeCourierRepository.findTypeCourierByName(newTypeCourierStr);
            if (newTypeCourierOptional.isEmpty()) {
                throw new Exception("TypeCourier with name " + newTypeCourierStr + "is not exist!");
            }
            TypeCourier newTypeCourier = newTypeCourierOptional.get();

            if (originalCourier.getTypeCourier().getCapacity() > newTypeCourier.getCapacity()) {
                List<Contract> contractListForRemove =
                        contractRepository.getContractsForRemoveByCapacity(courierId, newTypeCourier.getCapacity());
                for (Contract contract : contractListForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
            }
            originalCourier.setTypeCourier(newTypeCourier);
        }

        List<Integer> newRegionListStr = courierDto.getRegionList();
        if (newRegionListStr != null) {
            Set<Region> newRegionList = regionService.addIfNotExistsRegions(
                    courierDto.getRegionList()
                            .stream()
                            .map(num -> new Region().setNumberRegion(num))
                            .collect(Collectors.toSet())
            );

            if (!originalCourier.getRegionList().containsAll(newRegionList)) {
                List<Contract> contractListForRemove =
                        contractRepository.getContractsForRemoveByRegion(
                                courierId,
                                newRegionList
                                        .stream()
                                        .map(Region::getId)
                                        .toList()
                        );
                for (Contract contract : contractListForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
                originalCourier.setRegionList(newRegionList);
            }
        }

        List<String> newTimePeriodListStr = courierDto.getTimePeriodList();
        if (newTimePeriodListStr != null) {
            Set<TimePeriod> newTimePeriodList = timePeriodService.addIfNotExistsTimePeriods(
                    courierDto.getTimePeriodList()
                            .stream()
                            .map(DateTimeUtils::toTP)
                            .collect(Collectors.toSet())
            );

            if (!originalCourier.getTimePeriodList().containsAll(newTimePeriodList)) {
                List<Contract> contractListForRemove =
                        contractRepository.getContractsForRemoveByTimePeriod(
                                courierId,
                                newTimePeriodList
                                        .stream()
                                        .map(TimePeriod::getId)
                                        .toList()
                        );
                for (Contract contract : contractListForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
                originalCourier.setTimePeriodList(newTimePeriodList);
            }
        }

        courierDto = courierMapper.toCourierDto(originalCourier);
        return courierDto;
    }

    public CourierDto getCourierInfoById(int courierId) throws Exception {

        if (!courierRepository.existsById(courierId)) {
            throw new Exception("Courier does not exists with ID " + courierId);
        }

        CourierDto courierDto = courierMapper.toCourierDto(courierRepository.getById(courierId));

        int hs = 60 * 60;
        int t = courierRepository.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId).orElse(hs);
        float rating = (float) (hs - Math.min(t, hs)) / hs * 5;
        courierDto.setRating(rating);

        float earnings =
                courierRepository.getEarningsByCourierIdAndAwardForContract(
                        courierId
                        , GlobalConstant.AWARD_FOR_CONTRACT
                ).orElse(0);
        courierDto.setEarnings(earnings);

        return courierDto;
    }

    private Courier addNewCourier(CourierDto courierDto) throws Exception {

        int courierId = courierDto.getId();
        String nameType = courierDto.getType();

        if (!typeCourierRepository.existsByName(nameType) || courierRepository.existsById(courierId)) {
            throw new Exception();
        }

        Courier courier = new Courier();
        courier.setId(courierId);

        TypeCourier typeCourier = typeCourierRepository.getTypeCourierByName(nameType);
        courier.setTypeCourier(typeCourier);

        Set<Region> regions = regionService.addIfNotExistsRegions(
                courierDto.getRegionList()
                        .stream()
                        .map(num -> new Region().setNumberRegion(num))
                        .collect(Collectors.toSet()));
        courier.setRegionList(regions);

        Set<TimePeriod> timePeriods = timePeriodService.addIfNotExistsTimePeriods(
                courierDto.getTimePeriodList()
                        .stream()
                        .map(DateTimeUtils::toTP)
                        .collect(Collectors.toSet()));
        courier.setTimePeriodList(timePeriods);

        return courierRepository.save(courier);
    }

}
