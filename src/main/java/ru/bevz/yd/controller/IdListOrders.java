package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IdListOrders {

    @JsonProperty("orders")
    private IdList idOrders;

}
