package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.CourierIdList;
import ru.bevz.yd.controller.IdList;

@Data
@Accessors(chain = true)
public class CouriersCreatedResponse {

    @JsonProperty("couriers")
    private IdList couriers;

}
