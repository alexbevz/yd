package ru.bevz.yd.dto.mapper;

import org.springframework.stereotype.Component;
import ru.bevz.yd.controller.request.ContractInfo;
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

    public Contract toContract(ContractDto contractDto) {
        return new Contract()
                .setId(contractDto.getId())
                .setWeight(contractDto.getWeight())
                .setRegion(new Region().setNumberRegion(contractDto.getRegion()))
                .setTimePeriodList(
                        contractDto.getTimePeriodList()
                                .stream()
                                .map(DateTimeUtils::toTimePeriod)
                                .collect(Collectors.toSet()));
    }
}
