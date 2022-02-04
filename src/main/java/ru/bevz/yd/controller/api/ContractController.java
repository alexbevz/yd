package ru.bevz.yd.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер заказов", description = "Обслуживает запросы связанные с заказами")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ValidAndNotValidIdListsMapper validMapper;

    @PostMapping("")
    @Operation(
            summary = "Добавление заказов",
            description = "Позволяет добавить новые заказы в базу данных"
    )
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
    @Operation(
            summary = "Назначение заказов",
            description = "Позволяет назначить заказы выбранному курьеру по его характеристиками"
    )
    public ResponseEntity<Object> assignContracts(@RequestBody CourierInfo courierInfo) throws Exception {
        int idCourier = courierInfo.getId();
        ContractDto contractDto = contractService.assignContracts(idCourier);

        return ResponseEntity.status(HttpStatus.OK)
                .body(contractMapper.toContractsAssignOKResponse(contractDto));
    }

    @PostMapping("/complete")
    @Operation(
            summary = "Выполнение заказа",
            description = "Позволяет отметить заказ выполненным для опредленного курьера"
    )
    public ResponseEntity<Object> completeContract(@RequestBody CompletedContractRequest completedContractRequest) throws Exception {
        ContractDto contractDto =
                contractService.completeContract(contractMapper.toContractDto(completedContractRequest));


        return ResponseEntity.status(HttpStatus.OK)
                .body(contractMapper.toContractCompleteOKResponse(contractDto));
    }

}
