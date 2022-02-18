package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CouriersRequest {

    @JsonProperty("data")
    private Set<CourierInfo> courierInfos = new HashSet<>();

}
