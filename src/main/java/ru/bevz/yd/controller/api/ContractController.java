package ru.bevz.yd.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bevz.yd.controller.request.CompletedContractRequest;
import ru.bevz.yd.controller.request.ContractsRequest;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.response.ContractsBadRequestResponse;
import ru.bevz.yd.controller.response.ContractsCreatedResponse;
import ru.bevz.yd.dto.mapper.ContractMapper;
import ru.bevz.yd.dto.mapper.ValidAndNotValidIdListsMapper;
import ru.bevz.yd.dto.model.ContractDto;
import ru.bevz.yd.dto.model.ValidAndNotValidIdLists;
import ru.bevz.yd.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ValidAndNotValidIdListsMapper validMapper;

    @PostMapping("")
    public ResponseEntity<Object> createContracts(@RequestBody ContractsRequest contractsRequest) {
        List<ContractDto> contractDtoList = contractsRequest.getContractInfoList()
                .stream()
                .map(contractMapper::toContractDto)
                .toList();

        ValidAndNotValidIdLists valid = contractService.addNewContracts(contractDtoList).getValidLists();

        if (valid.hasNotValid()) {
            ContractsBadRequestResponse response = validMapper.toContractsBadRequestResponse(valid);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ContractsCreatedResponse response = validMapper.toContractsCreatedResponse(valid);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/assign")
    public ResponseEntity<Object> assignContracts(@RequestBody CourierInfo courierInfo) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/complete")
    public ResponseEntity<Object> completeContract(@RequestBody CompletedContractRequest completedContractRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
