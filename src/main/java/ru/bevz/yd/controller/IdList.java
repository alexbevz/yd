package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.util.List;

@Data
public class IdList {

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<Id> idList;

}
