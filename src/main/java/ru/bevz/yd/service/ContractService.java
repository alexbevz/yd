package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.model.Contract;
import ru.bevz.yd.model.Courier;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.model.StatusContract;
import ru.bevz.yd.repository.ContractRepository;
import ru.bevz.yd.repository.CourierRepository;
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

    public ContractDto assignContractsToCourierByCourierId(int courierId) {
        ContractDto contractDto = new ContractDto();
        Courier courier = courierRepository.getById(courierId);


        List<Contract> contractList =
                contractRepository.getAllByCourierAndStatus(courier, StatusContract.ASSIGNED);
        if (!contractList.isEmpty()) {
            contractDto.setDatetimeAssign(contractList.get(0).getDatetimeAssignment().toString());
            contractDto.setIdContractList(contractList.stream().map(Contract::getId).toList());
            return contractDto;
        }

        contractList = contractRepository.getAllByStatusAndRegionInAndWeightLessThanEqual(
                StatusContract.UNASSIGNED,
                courier.getRegionList(),
                courier.getTypeCourier().getCapacity()
        );


        contractDto.setDatetimeAssign(contractList.get(0).getDatetimeAssignment().toString());
        contractDto.setIdContractList(contractList.stream().map(Contract::getId).toList());

        return contractDto;
    }


}
