package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.controller.request.CompletedContractRequest;
import ru.bevz.yd.controller.request.ContractInfo;
import ru.bevz.yd.controller.response.ContractCompleteOKResponse;
import ru.bevz.yd.controller.response.ContractsAssignOKResponse;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.util.DateTimeUtils;

import java.util.stream.Collectors;

@Component
public class ContractMapper {

    public ContractDto toContractDto(ContractInfo contractInfo) {
        return new ContractDto()
                .setId(contractInfo.getId())
                .setWeight(contractInfo.getWeight())
                .setRegion(contractInfo.getRegion())
                .setTimePeriodList(contractInfo.getDeliveryHours());
    }

    public ContractDto toContractDto(CompletedContractRequest completedContractRequest) {
        return new ContractDto()
                .setId(completedContractRequest.getContractId())
                .setCourierId(completedContractRequest.getCourierId())
                .setDatetimeComplete(completedContractRequest.getDateTimeCompleted());
    }

    public Contract toContract(ContractDto contractDto) {
        return new Contract()
                .setId(contractDto.getId())
                .setWeight(contractDto.getWeight())
                .setRegion(new Region().setNumberRegion(contractDto.getRegion()))
                .setTimePeriodList(
                        contractDto.getTimePeriodList()
                                .stream()
                                .map(DateTimeUtils::toTP)
                                .collect(Collectors.toSet()));
    }

    public ContractsAssignOKResponse toContractsAssignOKResponse(ContractDto contractDto) {
        return new ContractsAssignOKResponse()
                .setIdList(new IdList().setIdList(contractDto.getIdContractList()
                        .stream()
                        .map(id -> new Id().setId(id))
                        .toList()))
                .setTimeAssigned(contractDto.getDatetimeAssign());
    }

    public ContractCompleteOKResponse toContractCompleteOKResponse(ContractDto contractDto) {
        return new ContractCompleteOKResponse()
                .setContractId(contractDto.getCourierId());
    }

}
