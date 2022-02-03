package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Transactional
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

        contractList = contractRepository.getContractsForAssigned(
                courier.getRegionList().stream().map(Region::getId).toList(),
                courier.getTimePeriodList().stream().map(TimePeriod::getId).toList(),
                courier.getTypeCourier().getCapacity()
        );

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

    //TODO: fix method
    @Transactional
    public ContractDto completeContract(ContractDto contractDto) throws Exception {
        int contractId = contractDto.getId();
        int courierId = contractDto.getCourierId();
        LocalDateTime dateTimeComplete =
                LocalDateTime.parse(contractDto.getDatetimeComplete());

        Optional<Contract> contractOptional = contractRepository.findById(contractId);

        if (contractOptional.isEmpty()) {
            throw new Exception("Not exists contract with id " + contractId + "!");
        }

        Contract contract = contractOptional.get();
        Optional<Courier> courierOptional = courierRepository.findById(courierId);

        if (courierOptional.isPresent()
                && contract.getStatus() == StatusContract.ASSIGNED
                && contract.getCourier().getId() == courierId
        ) {
            Courier courier = courierOptional.get();

            Optional<TimePeriod> timePeriodOptional = contract.getTimePeriodList()
                    .stream()
                    .filter(tp -> tp.getFrom().isBefore(dateTimeComplete.toLocalTime())
                            && tp.getTo().isAfter(dateTimeComplete.toLocalTime()))
                    .findFirst();

            if (timePeriodOptional.isEmpty()) {
                throw new Exception("Time is not valid! Has not found a time interval!");
            }

            Optional<Contract> lastContract =
                    contractRepository.getLastCompletedContract(courierId);

            LocalDateTime dateTimeRealizationStart;

            if (lastContract.isEmpty()
                    || dateTimeComplete.toLocalDate() == contract.getDatetimeRealization().toLocalDate()){
                dateTimeRealizationStart = LocalDateTime.of();
            } else {
                dateTimeRealizationStart = lastContract.get().getDatetimeRealization();
            }

            contract.setDatetimeRealizationStart(dateTimeRealizationStart);
            contract.setDatetimeRealization(LocalDateTime.now());
            contract.setStatus(StatusContract.COMPLETED);
            contract.setTypeCourier(courier.getTypeCourier());

        }

        return contractDto;
    }

}
