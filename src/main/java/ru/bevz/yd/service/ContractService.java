package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private CourierRepository courierRepository;

    @Transactional
    public ContractDto addNewContracts(List<ContractDto> contractDtoList) {

        ValidAndNotValidIdLists validLists = new ValidAndNotValidIdLists();

        for (ContractDto contractDto : contractDtoList) {
            try {
                Contract contract = addNewContract(contractDto);
                validLists.addValidId(contract.getId());
            } catch (Exception e) {
                validLists.addNotValidId(contractDto.getId());
            }
        }

        ContractDto contractDto = new ContractDto()
                .setValidLists(validLists);

        if (contractDto.getValidLists().hasNotValid()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return contractDto;
    }

    private Contract addNewContract(ContractDto contractDto) throws Exception {

        int contractId = contractDto.getId();

        if (contractRepository.existsById(contractId)) {
            throw new Exception();
        }


        Contract contract = new Contract()
                .setId(contractId)
                .setWeight(contractDto.getWeight())
                .setRegion(regionService.addIfNotExistsRegion(new Region()
                        .setNumberRegion(contractDto.getRegion())))
                .setTimePeriodList(timePeriodService.addIfNotExistsTimePeriods(
                        contractDto.getTimePeriodList()
                                .stream()
                                .map(DateTimeUtils::toTimePeriod)
                                .collect(Collectors.toSet())))
                .setStatus(StatusContract.UNASSIGNED);

        return contractRepository.save(contract);
    }

    //TODO: fix method
    public ContractDto assignContracts(int courierId) throws Exception {
        ContractDto contractDto = new ContractDto();
        Courier courier = courierRepository.findById(courierId).orElse(null);

        if (courier == null) {
            throw new Exception("Not exist courier with id " + courierId + "!");
        }


        List<Contract> contractList =
                contractRepository.getAllByCourierAndStatus(courier, StatusContract.ASSIGNED);

        if (!contractList.isEmpty()) {
            contractDto.setDatetimeAssign(contractList.get(0).getDatetimeAssignment().toString());
            contractDto.setIdContractList(contractList.stream().map(Contract::getId).toList());
            return contractDto;
        }

        contractList = contractRepository.getAllForCourier(
                courier.getRegionList().stream().map(Region::getId).toList(),
                courier.getTypeCourier().getCapacity()
        );

        contractList = getContractsWithCrossTimePeriodList(contractList, courier.getTimePeriodList());

        if (contractList.isEmpty()) {
            return contractDto;
        }

        LocalDateTime dateTime = LocalDateTime.now();

        for (Contract contract : contractList) {
            contract.setDatetimeAssignment(dateTime);
            contract.setStatus(StatusContract.ASSIGNED);
            contract.setCourier(courier);
        }


        contractDto.setDatetimeAssign(contractList.get(0).getDatetimeAssignment().toString());
        contractDto.setIdContractList(contractList.stream().map(Contract::getId).toList());

        return contractDto;
    }


    private List<Contract> getContractsWithCrossTimePeriodList(
            List<Contract> contractList,
            Set<TimePeriod> timePeriodList
    ) {
        Set<Contract> validContractList = new HashSet<>();


        for (TimePeriod timePeriodCourier : timePeriodList) {
            for (Contract contract : contractList) {
                for (TimePeriod timePeriodContract : contract.getTimePeriodList()) {
                    if (timePeriodCourier.getFrom().isBefore(timePeriodContract.getFrom())
                            && timePeriodCourier.getTo().isAfter(timePeriodContract.getFrom())
                            || timePeriodCourier.getFrom().isBefore(timePeriodContract.getTo())
                            && timePeriodCourier.getTo().isAfter(timePeriodContract.getTo())
                            || timePeriodCourier.getFrom().isAfter(timePeriodContract.getFrom())
                            && timePeriodCourier.getTo().isBefore(timePeriodContract.getTo())
                    ) {
                        validContractList.add(contract);
                        break;
                    }
                }
            }
        }

        return validContractList.stream().toList();
    }
}
