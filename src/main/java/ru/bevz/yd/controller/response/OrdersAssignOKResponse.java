package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdList;

@Data
@Accessors(chain = true)
public class OrdersAssignOKResponse {

    @JsonProperty("couriers")
    private IdList idList;

    @JsonProperty("assign_time")
    private String timeAssigned;

}
