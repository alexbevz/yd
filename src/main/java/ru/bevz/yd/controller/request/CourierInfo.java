package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class CourierInfo {

    @JsonProperty("courier_id")
    private int id;

    @JsonProperty("courier_type")
    private String courierType;

    @JsonProperty("regions")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private Set<Integer> regions = new HashSet<>();

    @JsonProperty("working_hours")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private Set<String> workingHours = new HashSet<>();

}
