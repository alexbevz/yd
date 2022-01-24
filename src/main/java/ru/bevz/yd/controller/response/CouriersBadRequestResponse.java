package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdListCouriers;

@Data
@Accessors(chain = true)
public class CouriersBadRequestResponse {

    @JsonProperty("validation_error")
    private IdListCouriers couriers;

}
