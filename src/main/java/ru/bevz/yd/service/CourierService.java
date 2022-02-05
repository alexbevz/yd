package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.constants.GlobalConstant;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.TypeCourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourierService {

    @Autowired
    private ContractRepository contractRepository;

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

    private float getEarningsCourier(int courierId) {
        float earnings = courierRepository.getEarningsByCourierIdAndAwardForContract(
                courierId,
                GlobalConstant.AWARD_FOR_CONTRACT
        ).orElse(0);
        return earnings;
    }

    private float getRatingCourier(int courierId) {
        int hs = 60 * 60;
        int t = courierRepository.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId).orElse(hs);
        float rating = (float) (hs - Math.min(t, hs)) / hs * 5;
        return rating;
    }

    @Transactional
    public CourierDTO addNewCouriers(List<CourierDTO> courierDTOs) throws Exception {

        List<Integer> courierIdsNotValid = new ArrayList<>();

        for (CourierDTO courierDto : courierDTOs) {
            try {
                addNewCourier(courierDto);
            } catch (Exception e) {
                courierIdsNotValid.add(courierDto.getId());
            }
        }

        if (!courierIdsNotValid.isEmpty()) {
            throw new Exception();
        }

        CourierDTO courierDTO = new CourierDTO()
                .setIdCouriers(
                        courierDTOs
                                .stream()
                                .map(CourierDTO::getId)
                                .toList()
                );

        return courierDTO;
    }

    @Transactional
    public CourierDTO addNewCourier(CourierDTO courierDTO) throws Exception {
        int courierId = courierDTO.getId();
        String nameType = courierDTO.getType();

        if (courierRepository.existsById(courierId)) {
            throw new Exception("Courier with id " + courierId + " exists!");
        }

        Optional<TypeCourier> typeCourierOptional =
                typeCourierRepository.findTypeCourierByName(nameType);
        if (typeCourierOptional.isEmpty()) {
            throw new Exception("TypeCourier with name " + nameType + "does not exist");
        }

        Courier courier = new Courier();
        courier.setId(courierId);

        TypeCourier typeCourier = typeCourierOptional.get();
        courier.setTypeCourier(typeCourier);

        Set<Region> regions = regionService.addIfNotExistsRegions(
                courierDTO.getRegions()
                        .stream()
                        .map(num -> new Region().setNumber(num))
                        .collect(Collectors.toSet())
        );
        courier.setRegions(regions);

        Set<TimePeriod> timePeriods = timePeriodService.addIfNotExistsTimePeriods(
                courierDTO.getTimePeriods()
                        .stream()
                        .map(DateTimeUtils::toTP)
                        .collect(Collectors.toSet())
        );
        courier.setTimePeriods(timePeriods);

        return courierMapper.toCourierDto(courierRepository.save(courier));
    }

    @Transactional
    public CourierDTO patchCourier(CourierDTO courierDTO) throws Exception {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRepository.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new Exception("Courier with id " + courierId + "do not exist!");
        }
        Courier originalCourier = courierOptional.get();

        String newTypeCourierStr = courierDTO.getType();
        if (newTypeCourierStr != null) {
            Optional<TypeCourier> newTypeCourierOptional =
                    typeCourierRepository.findTypeCourierByName(newTypeCourierStr);
            if (newTypeCourierOptional.isEmpty()) {
                throw new Exception("TypeCourier with name " + newTypeCourierStr + "is not exist!");
            }
            TypeCourier newTypeCourier = newTypeCourierOptional.get();

            if (originalCourier.getTypeCourier().getCapacity() > newTypeCourier.getCapacity()) {
                List<Contract> contractsForRemove =
                        contractRepository.getContractsForRemoveByCapacity(courierId, newTypeCourier.getCapacity());
                for (Contract contract : contractsForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
            }
            originalCourier.setTypeCourier(newTypeCourier);
        }

        List<Integer> newRegionListStr = courierDTO.getRegions();
        if (newRegionListStr != null) {
            Set<Region> newRegions = regionService.addIfNotExistsRegions(
                    courierDTO.getRegions()
                            .stream()
                            .map(num -> new Region().setNumber(num))
                            .collect(Collectors.toSet())
            );

            if (!originalCourier.getRegions().containsAll(newRegions)) {
                List<Contract> contractListForRemove =
                        contractRepository.getContractsForRemoveByRegion(
                                courierId,
                                newRegions
                                        .stream()
                                        .map(Region::getId)
                                        .toList()
                        );
                for (Contract contract : contractListForRemove) {
                    contract.setStatus(StatusContract.UNASSIGNED);
                    contract.setCourier(null);
                    contract.setDatetimeAssignment(null);
                }
                originalCourier.setRegions(newRegions);
            }
        }

        List<String> newTimePeriodListStr = courierDTO.getTimePeriods();
        if (newTimePeriodListStr != null) {
            Set<TimePeriod> newTimePeriods = timePeriodService.addIfNotExistsTimePeriods(
                    courierDTO.getTimePeriods()
                            .stream()
                            .map(DateTimeUtils::toTP)
                            .collect(Collectors.toSet())
            );

            if (!originalCourier.getTimePeriods().containsAll(newTimePeriods)) {
                List<Contract> contractsForRemove =
                        contractRepository.getContractsForRemoveByTimePeriod(
                                courierId,
                                newTimePeriods
                                        .stream()
                                        .map(TimePeriod::getId)
                                        .toList()
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

    public CourierDTO getCourierInfoById(CourierDTO courierDTO) throws Exception {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRepository.findById(courierId);

        if (courierOptional.isEmpty()) {
            throw new Exception("Courier does not exists with ID " + courierId);
        }

        courierDTO = courierMapper.toCourierDto(courierOptional.get());
        courierDTO.setRating(getRatingCourier(courierId));
        courierDTO.setEarnings(getEarningsCourier(courierId));

        return courierDTO;
    }

}
