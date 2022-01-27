package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ContractCompleteOKResponse {

    @JsonProperty("order_id")
    private int contractId;

}
