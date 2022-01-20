package ru.bevz.yd.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bevz.yd.controller.request.CourierInfo;
import ru.bevz.yd.controller.request.CouriersRequest;
import ru.bevz.yd.service.CourierService;

@RestController
@RequestMapping("/couriers")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @PostMapping("")
    public ResponseEntity<Object> createCouriers(@RequestBody CouriersRequest couriersRequest) {

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchCourier(@PathVariable(value = "id") long courierId, @RequestBody CourierInfo courierInfo) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCourier(@PathVariable(value = "id") long courierId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
