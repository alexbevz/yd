package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bevz.yd.controller.IdListCouriers;

@Data
public class CouriersBadRequestResponse {

    @JsonProperty("validation_error")
    private IdListCouriers couriers;

}
