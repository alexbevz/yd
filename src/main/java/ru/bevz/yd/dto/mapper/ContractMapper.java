package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.request.CompletedContractRequest;
import ru.bevz.yd.controller.request.ContractInfo;
import ru.bevz.yd.controller.response.ContractsAssignOKResponse;
import ru.bevz.yd.controller.response.ContractsCompleteOKResponse;
import ru.bevz.yd.controller.response.ContractsCreatedResponse;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.util.DateTimeUtils;

@Component
public class ContractMapper {

    public ContractDTO toContractDTO(ContractInfo contractInfo) {
        return new ContractDTO()
                .setId(contractInfo.getId())
                .setWeight(contractInfo.getWeight())
                .setRegion(contractInfo.getRegion())
                .setTimePeriods(contractInfo.getDeliveryHours());
    }

    public ContractDTO toContractDTO(CompletedContractRequest completedContractRequest) {
        return new ContractDTO()
                .setId(completedContractRequest.getContractId())
                .setCourierId(completedContractRequest.getCourierId())
                .setDatetimeComplete(completedContractRequest.getDateTimeCompleted());
    }

    public ContractDTO toContractDTO(Contract contract) {
        return new ContractDTO()
                .setId(contract.getId())
                .setCourierId(contract.getCourier().getId())
                .setRegion(contract.getRegion().getNumber())
                .setWeight(contract.getWeight())
                .setTimePeriods(
                        contract.getTimePeriods()
                                .stream()
                                .map(DateTimeUtils::toStringTP)
                                .toList()
                )
                .setDatetimeAssign(contract.getDatetimeAssignment().toString())
                .setDatetimeComplete(contract.getDatetimeRealization().toString());

    }

    public ContractsAssignOKResponse toContractsAssignOKResponse(ContractDTO contractDTO) {
        return new ContractsAssignOKResponse()
                .setIdContracts(
                        new IdList()
                                .setIdList(
                                        contractDTO.getIdContracts()
                                                .stream()
                                                .map(id -> new Id().setId(id))
                                                .toList()
                                )
                )
                .setTimeAssigned(contractDTO.getDatetimeAssign());
    }

    public ContractsCompleteOKResponse toContractCompleteOKResponse(ContractDTO contractDTO) {
        return new ContractsCompleteOKResponse()
                .setContractId(contractDTO.getCourierId());
    }

    public ContractsCreatedResponse toContractsCreatedResponse(ContractDTO contractDTO) {
        return new ContractsCreatedResponse()
                .setContracts(
                        new IdList()
                                .setIdList(
                                        contractDTO.getIdContracts()
                                                .stream()
                                                .map(id -> new Id().setId(id))
                                                .toList()
                                )
                );
    }

}
