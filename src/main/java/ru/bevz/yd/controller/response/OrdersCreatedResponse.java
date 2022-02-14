package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdList;

@Data
@Accessors(chain = true)
public class OrdersCreatedResponse {

    @JsonProperty("orders")
    @Schema(description = "Список заказов")
    private IdList orders;

}
