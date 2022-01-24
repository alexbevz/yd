package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IdListOrders {

    @JsonProperty("orders")
    private IdList idOrders;

}
