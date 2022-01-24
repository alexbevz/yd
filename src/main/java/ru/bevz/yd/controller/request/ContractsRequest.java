package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContractsRequest {

    @JsonProperty("data")
    private List<ContractInfo> contractInfoList;

}
