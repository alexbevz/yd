package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bevz.yd.controller.IdList;

@Data
public class OrdersAssignOKResponse {


    @JsonProperty("couriers")
    private IdList idList;

    @JsonProperty("assign_time")
    private String timeAssigned;

}
