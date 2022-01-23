package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CourierInfo {

    @JsonProperty("courier_id")
    private long id;

    @JsonProperty("courier_type")
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("regions")
    private List<Integer> regionNumberList;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("working_hours")
    //TODO: Will write the own valid annotation
    private List<@Pattern(regexp = "^(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})-(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})$") String> timePeriodList;

}
