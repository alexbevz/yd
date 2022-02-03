package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class IdList {

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<Id> idList = new ArrayList<>();

}
