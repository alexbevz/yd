package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class ContractInfo {

    @JsonProperty("order_id")
    private long contractId;

    @JsonProperty("weight")
    private float weight;

    @JsonProperty("region")
    private int region;

    @JsonProperty("delivery_hours")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<@Pattern(regexp = "^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})-(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$") String> timePeriodList;

}
