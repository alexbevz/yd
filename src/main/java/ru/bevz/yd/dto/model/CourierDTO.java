package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class CourierDTO {

    private int id;

    private String type;

    private List<Integer> regions = new ArrayList<>();

    private List<String> timePeriods = new ArrayList<>();

    private List<Integer> idCouriers = new ArrayList<>();

    private float rating;

    private float earnings;

}
