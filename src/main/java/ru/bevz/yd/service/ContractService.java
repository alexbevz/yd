package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.dto.mapper.ContractMapper;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.exception.*;
import ru.bevz.yd.model.*;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bevz.yd.util.DateTimeUtils.findTPForTime;
import static ru.bevz.yd.util.DateTimeUtils.isTimeInTP;

@Service
public class ContractService {

    @Autowired
    private SecondaryService secondaryServ;

    @Autowired
    private ContractRepository contractRep;

    @Autowired
    private CourierRepository courierRep;

    @Autowired
    private ContractMapper contractMapper;

    @Transactional
    public ContractDTO addContracts(List<ContractDTO> contractDTOs) {

        List<Integer> notValidContractsId = new ArrayList<>();

        for (ContractDTO contractDto : contractDTOs) {
            try {
                addNewContract(contractDto);
            } catch (Exception e) {
                notValidContractsId.add(contractDto.getId());
                e.printStackTrace();
            }
        }

        if (!notValidContractsId.isEmpty()) {
            throw new NotValidObjectsException("orders", notValidContractsId);
        }

        return new ContractDTO().setIdContracts(
                contractDTOs
                        .stream()
                        .map(ContractDTO::getId)
                        .toList()
        );
    }

    @Transactional
    public ContractDTO addNewContract(ContractDTO contractDTO) {

        int contractId = contractDTO.getId();

        Optional<Contract> optionalContract = contractRep.findById(contractId);

        if (optionalContract.isPresent()) {
            throw new EntityAlreadyExistsException(optionalContract.get());
        }

        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setWeight(contractDTO.getWeight());
        contract.setRegion(secondaryServ.getOrSaveRegionByNumber(contractDTO.getRegion()));
        contract.setTimePeriods(secondaryServ.getOrSaveTimePeriodsByString(contractDTO.getTimePeriods()));
        contract.setStatus(StatusContract.UNASSIGNED);

        return contractMapper.toContractDTO(contractRep.save(contract));
    }

    @Transactional
    public ContractDTO assignContracts(ContractDTO contractDTO) {
        int courierId = contractDTO.getCourierId();

        contractDTO = new ContractDTO();
        Optional<Courier> optionalCourier = courierRep.findById(courierId);

        if (optionalCourier.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }

        Courier courier = optionalCourier.get();

        Set<Contract> contracts =
                contractRep.getAllByCourierAndStatus(courier, StatusContract.ASSIGNED);

        if (!contracts.isEmpty()) {
            contractDTO.setDatetimeAssign(
                    contracts
                            .stream()
                            .findAny()
                            .get()
                            .getDatetimeAssignment()
                            .toString()
            );
            contractDTO.setIdContracts(contracts.stream().map(Contract::getId).toList());
            return contractDTO;
        }

        contracts = contractRep.getContractsForAssigned(
                courier.getRegions()
                        .stream()
                        .map(Region::getId)
                        .collect(Collectors.toSet()),
                courier.getTimePeriods()
                        .stream()
                        .map(TimePeriod::getId)
                        .collect(Collectors.toSet()),
                courier.getTypeCourier().getCapacity()
        );

        if (contracts.isEmpty()) {
            return contractDTO;
        }

        LocalDateTime dateTimeAssignment = LocalDateTime.now();

        for (Contract contract : contracts) {
            contract.setDatetimeAssignment(dateTimeAssignment);
            contract.setStatus(StatusContract.ASSIGNED);
            contract.setCourier(courier);
        }

        contractDTO.setDatetimeAssign(dateTimeAssignment.toString());
        contractDTO.setIdContracts(
                contracts
                        .stream()
                        .map(Contract::getId)
                        .toList()
        );

        return contractDTO;
    }

    @Transactional
    public ContractDTO completeContract(ContractDTO contractDTO) {
        int contractId = contractDTO.getId();
        int courierId = contractDTO.getCourierId();
        LocalDateTime DTCompleted =
                LocalDateTime.parse(contractDTO.getDatetimeComplete());

        Optional<Contract> contractOptional = contractRep.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new EntityNotExistsException(new Contract().setId(contractId));
        }
        Contract contract = contractOptional.get();
        if (contract.getStatus() == StatusContract.COMPLETED) {
            throw new OrderHasBeenDeliveredException(contract);
        }

        Optional<Courier> courierOptional = courierRep.findById(courierId);
        if (courierOptional.isEmpty()) {
            throw new EntityNotExistsException(new Courier().setId(courierId));
        }
        Courier courier = courierOptional.get();

        if (contract.getCourier().getId() != courierId) {
            throw new OrderAssignedForOtherCourierException(contract);
        }

        Optional<TimePeriod> courierTimePeriodOptional =
                findTPForTime(courier.getTimePeriods(), DTCompleted);
        if (courierTimePeriodOptional.isEmpty()) {
            throw new NotFoundTPForEntityException(courier);
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
