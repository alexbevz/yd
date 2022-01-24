package ru.bevz.yd.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
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
    private float rating;

    @JsonProperty("earnings")
    private float earnings;

}
