package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompletedOrderRequest {

    @JsonProperty("courier_id")
    private int courierId;

    @JsonProperty("order_id")
    private int orderId;

    @JsonProperty("complete_time")
    private String dateTimeCompleted;

}
