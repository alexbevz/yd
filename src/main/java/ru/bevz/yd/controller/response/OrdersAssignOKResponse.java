package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdList;

@Data
@Accessors(chain = true)
public class OrdersAssignOKResponse {

    @JsonProperty("orders")
    private IdList idOrders;

    @JsonProperty("assign_time")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String timeAssigned;

}
