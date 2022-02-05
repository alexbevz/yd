package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ContractIdList {

    @JsonProperty("orders")
    private IdList idContracts;

}
