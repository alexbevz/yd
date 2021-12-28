package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CompletedContractRequest {

    @JsonProperty("courier_id")
    private long courierId;

    @JsonProperty("order_id")
    private long contractId;

    @JsonProperty("complete_time")
    @DateTimeFormat
    private LocalDateTime dateTimeCompleted;

}
