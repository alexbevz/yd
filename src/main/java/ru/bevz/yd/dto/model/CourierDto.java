package ru.bevz.yd.dto.model;

import lombok.Data;

import java.util.List;

@Data
public class CourierDto {

    private long courierId;

    private String type;

    private List<Integer> regionList;

    private List<String> timePeriodList;
}
