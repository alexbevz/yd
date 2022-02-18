package ru.bevz.yd.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class IdList {

    @JsonValue
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @Schema(description = "Идентификатор")
    private Set<Id> idList;

    public IdList(Set<Integer> idList) {
        this.idList = idList.stream()
                .map(id -> new Id().setId(id))
                .collect(Collectors.toSet());
    }

}
