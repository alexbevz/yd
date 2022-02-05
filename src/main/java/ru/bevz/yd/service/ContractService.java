package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.CourierIdList;
import ru.bevz.yd.dto.mapper.ContractMapper;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bevz.yd.util.DateTimeUtils.findTPForTime;
import static ru.bevz.yd.util.DateTimeUtils.isTimeInTP;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRep;

    @Autowired
    private CourierRepository courierRep;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ContractMapper contractMapper;

    @Transactional
    public ContractDTO addContracts(List<ContractDTO> contractDTOs) throws Exception {

        List<Integer> contractIdsNotValid = new ArrayList<>();

        for (ContractDTO contractDto : contractDTOs) {
            try {
                addNewContract(contractDto);
            } catch (Exception e) {
                contractIdsNotValid.add(contractDto.getId());
            }
        }

        if (!contractIdsNotValid.isEmpty()) {
            throw new Exception(
                    new CourierIdList()
                            .setIdCouriers(
                                    new IdList().setIdList(
                                            contractIdsNotValid
                                                    .stream()
                                                    .map(id -> new Id().setId(id))
                                                    .toList()
                                    )
                            )
                            .toString()
            );
        }
        ContractDTO contractDTO = new ContractDTO().setIdContracts(
                contractDTOs
                        .stream()
                        .map(ContractDTO::getId)
                        .toList()
        );

        return contractDTO;
    }

    @Transactional
    public ContractDTO addNewContract(ContractDTO contractDTO) throws Exception {

        int contractId = contractDTO.getId();

        if (contractRep.existsById(contractId)) {
            throw new Exception();
        }


        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setWeight(contractDTO.getWeight());
        contract.setRegion(regionService.addIfNotExistsRegion(new Region()
                .setNumber(contractDTO.getRegion())));
        contract.setTimePeriods(timePeriodService.addIfNotExistsTimePeriods(
                contractDTO.getTimePeriods()
                        .stream()
                        .map(DateTimeUtils::toTP)
                        .collect(Collectors.toSet())));
        contract.setStatus(StatusContract.UNASSIGNED);

        return contractMapper.toContractDTO(contractRep.save(contract));
    }

    @Transactional
    public ContractDTO assignContracts(ContractDTO contractDTO) throws Exception {
        int courierId = contractDTO.getCourierId();

        contractDTO = new ContractDTO();
        Courier courier = courierRep.findById(courierId).orElse(null);

        if (courier == null) {
            throw new Exception("Not exist courier with id " + courierId + "!");
        }


        List<Contract> contracts =
                contractRep.getAllByCourierAndStatus(courier, StatusContract.ASSIGNED);

        if (!contracts.isEmpty()) {
            contractDTO.setDatetimeAssign(contracts.get(0).getDatetimeAssignment().toString());
            contractDTO.setIdContracts(contracts.stream().map(Contract::getId).toList());
            return contractDTO;
        }

        contracts = contractRep.getContractsForAssigned(
                courier.getRegions().stream().map(Region::getId).toList(),
                courier.getTimePeriods().stream().map(TimePeriod::getId).toList(),
                courier.getTypeCourier().getCapacity()
        );

        if (contracts.isEmpty()) {
            return contractDTO;
        }

        LocalDateTime dateTime = LocalDateTime.now();

        for (Contract contract : contracts) {
            contract.setDatetimeAssignment(dateTime);
            contract.setStatus(StatusContract.ASSIGNED);
            contract.setCourier(courier);
        }

        contractDTO.setDatetimeAssign(contracts.get(0).getDatetimeAssignment().toString());
        contractDTO.setIdContracts(contracts.stream().map(Contract::getId).toList());

        return contractDTO;
    }

    @Transactional
    public ContractDTO completeContract(ContractDTO contractDTO) throws Exception {
        int contractId = contractDTO.getId();
        int courierId = contractDTO.getCourierId();
        LocalDateTime DTCompleted =
                LocalDateTime.parse(contractDTO.getDatetimeComplete());

        Optional<Contract> contractOptional = contractRep.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new Exception("Not exists the contract with id " + contractId + "!");
        }
        Contract contract = contractOptional.get();
        if (contract.getStatus() == StatusContract.COMPLETED) {
            throw new Exception("The contract with id " + contractId + " has already been delivered!");
        }

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new Exception("Not exists the courier with id " + courierId + "!");
        }
        Courier courier = courierOptional.get();

        if (contract.getCourier().getId() != courierId) {
            throw new Exception("The contract with id " + contractId + " is not assigned to courier with id " + courierId + "!");
        }

        Optional<TimePeriod> courierTimePeriodOptional =
                findTPForTime(courier.getTimePeriods(), DTCompleted);
        if (courierTimePeriodOptional.isEmpty()) {
            throw new Exception("Time is not valid! " +
                    "Has not found a time interval fot courier with id " + courierId + "! "
                    + courier.getTimePeriods()
                    .stream()
                    .map(DateTimeUtils::toStringTP)
                    .toList());
        }
        TimePeriod courierTP = courierTimePeriodOptional.get();

        Optional<Contract> lastContractOptional =
                contractRep.getLastCompletedContract(courierId, DTCompleted.toLocalDate());

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

        return contractDTO;
    }

}
