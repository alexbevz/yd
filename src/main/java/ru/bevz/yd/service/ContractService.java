package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.StatusContract;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.util.DateTimeUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TimePeriodService timePeriodService;

    @Autowired
    private RegionService regionService;

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

}
