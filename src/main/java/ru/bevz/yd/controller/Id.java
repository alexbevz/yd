package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Id {

    @JsonProperty("id")
    private int id;

}
