package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CourierInfo {

    @JsonProperty("courier_id")
    private long courierId;

    @JsonProperty("courier_type")
    private String typeCourier;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("regions")
    private List<Integer> regionNumberList;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("working_hours")
    private List<@Pattern(regexp = "^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})-(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$") String> timePeriodList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("rating")
    private float rating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("earnings")
    private float earnings;

}
