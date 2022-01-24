package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrdersCompleteOKResponse {

    @JsonProperty("order_id")
    private int orderId;

}
