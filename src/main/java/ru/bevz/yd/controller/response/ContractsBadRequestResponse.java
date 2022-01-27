package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdListContracts;

@Data
@Accessors(chain = true)
public class ContractsBadRequestResponse {

    @JsonProperty("validation_error")
    private IdListContracts contracts;

}
