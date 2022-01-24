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
import ru.bevz.yd.repository.ContractRepository;

@RestController
@RequestMapping("/orders")
public class ContractController {

    @Autowired
    private ContractRepository contractRepository;

    @PostMapping("")
    public ResponseEntity<Object> createContracts(@RequestBody ContractsRequest contractsRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
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
