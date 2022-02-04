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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bevz.yd.util.DateTimeUtils.findTPForTime;
import static ru.bevz.yd.util.DateTimeUtils.isTimeInTP;

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
                                .map(DateTimeUtils::toTP)
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

    @Transactional
    public ContractDto completeContract(ContractDto contractDto) throws Exception {
        int contractId = contractDto.getId();
        int courierId = contractDto.getCourierId();
        LocalDateTime DTCompleted =
                LocalDateTime.parse(contractDto.getDatetimeComplete());

        Optional<Contract> contractOptional = contractRepository.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new Exception("Not exists the contract with id " + contractId + "!");
        }
        Contract contract = contractOptional.get();
        if (contract.getStatus() == StatusContract.COMPLETED) {
            throw new Exception("The contract with id " + contractId + " has already been delivered!");
        }

        Optional<Courier> courierOptional = courierRepository.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new Exception("Not exists the courier with id " + courierId + "!");
        }
        Courier courier = courierOptional.get();

        if (contract.getCourier().getId() != courierId) {
            throw new Exception("The contract with id " + contractId + " is not assigned to courier with id " + courierId + "!");
        }

        Optional<TimePeriod> courierTimePeriodOptional =
                findTPForTime(courier.getTimePeriodList(), DTCompleted);
        if (courierTimePeriodOptional.isEmpty()) {
            throw new Exception("Time is not valid! " +
                    "Has not found a time interval fot courier with id " + courierId + "! "
                    + courier.getTimePeriodList()
                    .stream()
                    .map(DateTimeUtils::toStringTP)
                    .toList());
        }
        TimePeriod courierTP = courierTimePeriodOptional.get();

        Optional<Contract> lastContractOptional =
                contractRepository.getLastCompletedContract(courierId, DTCompleted.toLocalDate());

        LocalDateTime dateTimeRealizationStart;

        if (lastContractOptional.isPresent()) {
            Contract lastContract = lastContractOptional.get();
            if (isTimeInTP(courierTP, lastContract.getDatetimeRealization())) {
                dateTimeRealizationStart = lastContract.getDatetimeRealization();
            } else {
                dateTimeRealizationStart =
                        LocalDateTime.of(DTCompleted.toLocalDate(), courierTP.getFrom());
            }
        } else {
            dateTimeRealizationStart =
                    LocalDateTime.of(DTCompleted.toLocalDate(), courierTP.getFrom());
        }

        contract.setStatus(StatusContract.COMPLETED);
        contract.setDatetimeRealizationStart(dateTimeRealizationStart);
        contract.setDatetimeRealization(DTCompleted);
        contract.setTypeCourier(courier.getTypeCourier());

        return contractDto;
    }

}
