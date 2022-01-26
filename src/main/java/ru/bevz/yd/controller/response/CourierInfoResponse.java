package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CourierInfoResponse {

    @JsonProperty("courier_id")
    private int id;

    @JsonProperty("courier_type")
    private String courierType;

    @JsonProperty("regions")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<Integer> regions;

    @JsonProperty("working_hours")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<String> workingHours;

    @JsonProperty("rating")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float rating;

    @JsonProperty("earnings")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float earnings;

}
