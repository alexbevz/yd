package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.dto.mapper.CourierMapper;
import ru.bevz.yd.dto.model.CourierDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.TimePeriod;
import ru.bevz.yd.model.TypeCourier;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.repository.RegionRepository;
import ru.bevz.yd.repository.TimePeriodRepository;
import ru.bevz.yd.repository.TypeCourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourierService {

    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private TypeCourierRepository typeCourierRepository;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TimePeriodService timePeriodService;

    @Transactional
    public ValidAndNotValidIdLists addNewCouriers(List<CourierDto> courierDtoList) {
        ValidAndNotValidIdLists valids = new ValidAndNotValidIdLists();

        for (CourierDto courierDto : courierDtoList) {
            try {
                Courier courier = addNewCourier(courierDto);
                valids.addValidId(courier.getId());
            } catch (Exception e) {
                valids.addNotValidId(courierDto.getId());
            }
        }

        if (valids.hasNotValid()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return valids;
    }

    public CourierDto patchCourier(CourierDto courierDto) {
        return new CourierDto();
    }

    public CourierDto getCourierInfo(CourierDto courierDto) {
        return new CourierDto();
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
                        .map(DateTimeUtils::toTimePeriod)
                        .collect(Collectors.toSet()));
        courier.setTimePeriodList(timePeriods);

        return courierRepository.save(courier);
    }

}
