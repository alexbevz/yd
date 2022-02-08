package ru.bevz.yd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.constants.GlobalConstant;
import ru.bevz.yd.controller.CourierIdList;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.RegionRepository;
import ru.bevz.yd.repository.TypeCourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourierService {

    @Autowired
    private ContractRepository contractRep;

    @Autowired
    private CourierRepository courierRep;

    @Autowired
    private TypeCourierRepository typeCourierRep;

    @Autowired
    private RegionRepository regionRep;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private CourierMapper courierMapper;

    public float getEarningsCourier(int courierId) {
        float earnings = courierRep.getEarningsByCourierIdAndAwardForContract(
                courierId,
                GlobalConstant.AWARD_FOR_CONTRACT
        ).orElse(0);
        return earnings;
    }

    public float getRatingCourier(int courierId) {
        int hs = 60 * 60;
        int t = courierRep.getMinAmongAvgTimeDeliveryRegionsByCourierId(courierId).orElse(hs);
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
            throw new Exception(
                    new ObjectMapper().writeValueAsString(
                            new CourierIdList().setIdCouriers(
                                    new IdList().setIdList(courierIdsNotValid
                                            .stream()
                                            .map(id -> new Id().setId(id))
                                            .toList()
                                    )
                            )
                    )
            );
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

        if (courierRep.existsById(courierId)) {
            throw new Exception("Courier with id " + courierId + " exists!");
        }

        Optional<TypeCourier> typeCourierOptional =
                typeCourierRep.findTypeCourierByName(nameType);
        if (typeCourierOptional.isEmpty()) {
            throw new Exception("TypeCourier with name " + nameType + "does not exist");
        }

        Courier courier = new Courier();
        courier.setId(courierId);

        TypeCourier typeCourier = typeCourierOptional.get();
        courier.setTypeCourier(typeCourier);

        Set<Region> regions = new HashSet<>();
        for (int number : courierDTO.getRegions()) {
            Optional<Region> regionOptional = regionRep.findRegionByNumber(number);
            regions.add(regionOptional.orElse(regionRep.save(new Region().setNumber(number))));
        }
        courier.setRegions(regions);

        Set<TimePeriod> timePeriods = timePeriodService.addIfNotExistsTimePeriods(
                courierDTO.getTimePeriods()
                        .stream()
                        .map(DateTimeUtils::toTP)
                        .collect(Collectors.toSet())
        );
        courier.setTimePeriods(timePeriods);

        return courierMapper.toCourierDto(courierRep.save(courier));
    }

    @Transactional
    public CourierDTO patchCourier(CourierDTO courierDTO) throws Exception {
        int courierId = courierDTO.getId();

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new Exception("Courier with id " + courierId + "do not exist!");
        }
        Courier originalCourier = courierOptional.get();

        String newTypeCourierStr = courierDTO.getType();
        if (newTypeCourierStr != null) {
            Optional<TypeCourier> newTypeCourierOptional =
                    typeCourierRep.findTypeCourierByName(newTypeCourierStr);
            if (newTypeCourierOptional.isEmpty()) {
                throw new Exception("TypeCourier with name " + newTypeCourierStr + "is not exist!");
            }
            TypeCourier newTypeCourier = newTypeCourierOptional.get();

            if (originalCourier.getTypeCourier().getCapacity() > newTypeCourier.getCapacity()) {
                List<Contract> contractsForRemove =
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
            Set<Region> newRegions = new HashSet<>();
            for (int number : courierDTO.getRegions()) {
                Optional<Region> regionOptional = regionRep.findRegionByNumber(number);
                newRegions.add(regionOptional.orElse(regionRep.save(new Region().setNumber(number))));
            }

            if (!originalCourier.getRegions().containsAll(newRegions)) {
                List<Contract> contractListForRemove =
                        contractRep.getContractsForRemoveByRegion(
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

        Set<String> newTimePeriodListStr = courierDTO.getTimePeriods();
        if (newTimePeriodListStr != null) {
            Set<TimePeriod> newTimePeriods = timePeriodService.addIfNotExistsTimePeriods(
                    courierDTO.getTimePeriods()
                            .stream()
                            .map(DateTimeUtils::toTP)
                            .collect(Collectors.toSet())
            );

            if (!originalCourier.getTimePeriods().containsAll(newTimePeriods)) {
                List<Contract> contractsForRemove =
                        contractRep.getContractsForRemoveByTimePeriod(
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

        Optional<Courier> courierOptional = courierRep.findById(courierId);

        if (courierOptional.isEmpty()) {
            throw new Exception("Courier does not exists with ID " + courierId);
        }

        courierDTO = courierMapper.toCourierDto(courierOptional.get());
        courierDTO.setRating(getRatingCourier(courierId));
        courierDTO.setEarnings(getEarningsCourier(courierId));

        return courierDTO;
    }

}
