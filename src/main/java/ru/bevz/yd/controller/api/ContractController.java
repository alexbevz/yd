package ru.bevz.yd.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ru.bevz.yd.controller.response.ContractsAssignOKResponse;
import ru.bevz.yd.controller.response.ContractsCompleteOKResponse;
import ru.bevz.yd.controller.response.ContractsCreatedResponse;
import ru.bevz.yd.dto.mapper.ContractMapper;
import ru.bevz.yd.dto.model.ContractDTO;
import ru.bevz.yd.service.ContractService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(
        name = "Контроллер заказов",
        description = "Обслуживает запросы связанные с заказами"
)
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractMapper contractMapper;

    @PostMapping("")
    @Operation(
            summary = "Добавление заказов",
            description = "Позволяет добавить новые заказы в базу данных"
    )
    @ApiResponse(
            responseCode = "201",
            content = @Content(schema = @Schema(implementation = ContractsCreatedResponse.class))
    )
    public ResponseEntity<Object> createContracts(@RequestBody ContractsRequest contractsRequest) throws

            Exception {
        List<ContractDTO> contractDTOs = contractsRequest.getContractInfos()
                .stream()
                .map(contractMapper::toContractDTO)
                .toList();

        ContractDTO courierDto = contractService.addContracts(contractDTOs);

        ContractsCreatedResponse response = contractMapper.toContractsCreatedResponse(courierDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/assign")
    @Operation(
            summary = "Назначение заказов",
            description = "Позволяет назначить заказы выбранному курьеру по его характеристиками"
    )
    public ResponseEntity<Object> assignContracts(@RequestBody CourierInfo courierInfo) throws Exception {

        ContractDTO contractDto = new ContractDTO().setCourierId(courierInfo.getId());

        contractDto = contractService.assignContracts(contractDto);

        ContractsAssignOKResponse response = contractMapper.toContractsAssignOKResponse(contractDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/complete")
    @Operation(
            summary = "Выполнение заказа",
            description = "Позволяет отметить заказ выполненным для опредленного курьера"
    )
    public ResponseEntity<Object> completeContract(@RequestBody CompletedContractRequest completedContractRequest) throws Exception {

        ContractDTO contractDto = contractMapper.toContractDTO(completedContractRequest);

        contractDto = contractService.completeContract(contractDto);

        ContractsCompleteOKResponse response = contractMapper.toContractCompleteOKResponse(contractDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
