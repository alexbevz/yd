package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdListCouriers;

@Data
@Accessors(chain = true)
public class CouriersCreatedResponse {

    @JsonValue
    private IdListCouriers couriers;

}
