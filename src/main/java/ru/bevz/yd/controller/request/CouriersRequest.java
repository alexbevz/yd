package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CouriersRequest {

    @JsonProperty("data")
    private List<CourierInfo> courierInfoList;

}
