package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompletedContractRequest {

    @JsonProperty("courier_id")
    private int courierId;

    @JsonProperty("order_id")
    private int contractId;

    @JsonProperty("complete_time")
    private LocalDateTime dateTimeCompleted;

}
