package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class CourierDTO {

    private int id;

    private String type;

    private Set<Integer> regions = new HashSet<>();

    private Set<String> timePeriods = new HashSet<>();

    private List<Integer> idCouriers = new ArrayList<>();

    private float rating;

    private float earnings;

}
