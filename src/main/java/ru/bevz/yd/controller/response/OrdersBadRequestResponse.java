package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bevz.yd.controller.IdListOrders;

@Data
public class OrdersBadRequestResponse {

    @JsonProperty("validation_error")
    private IdListOrders orders;

}
