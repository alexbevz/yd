package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdListOrders;

@Data
@Accessors(chain = true)
public class OrdersBadRequestResponse {

    @JsonProperty("validation_error")
    private IdListOrders orders;

}
