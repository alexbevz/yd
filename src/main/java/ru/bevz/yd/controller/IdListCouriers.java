package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IdListCouriers {

    @JsonProperty("couriers")
    private IdList idCouriers;

}
