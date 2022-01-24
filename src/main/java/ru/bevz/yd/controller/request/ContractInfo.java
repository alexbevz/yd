package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContractInfo {

    @JsonProperty("order_id")
    private int id;

    @JsonProperty("weight")
    private float weight;

    @JsonProperty("region")
    private int region;

    @JsonProperty("delivery_hours")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<String> deliveryHours;

}
