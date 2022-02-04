package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class IdList {

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @Schema(description = "Идентификатор")
    private List<Id> idList;

}
