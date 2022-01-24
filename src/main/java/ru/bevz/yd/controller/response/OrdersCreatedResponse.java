package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdList;

@Data
@Accessors(chain = true)
public class OrdersCreatedResponse {

    @JsonProperty("orders")
    private IdList orders;

}
