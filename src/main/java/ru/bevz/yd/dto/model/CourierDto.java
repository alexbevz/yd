package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CourierDto {

    private int id;

    private String type;

    private List<Integer> regionList;

    private List<String> timePeriodList;

    private float rating;

    private float earnings;

}
