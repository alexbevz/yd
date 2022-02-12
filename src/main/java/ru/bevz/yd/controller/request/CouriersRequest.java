package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CouriersRequest {

    @JsonProperty("data")
    private List<CourierInfo> courierInfos = new ArrayList<>();

}
