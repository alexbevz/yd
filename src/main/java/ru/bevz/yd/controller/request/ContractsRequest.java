package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ContractsRequest {

    @JsonProperty("data")
    @Schema(
            required = true,
            description = "Список заказов"
    )
    private List<ContractInfo> contractInfos;

}
