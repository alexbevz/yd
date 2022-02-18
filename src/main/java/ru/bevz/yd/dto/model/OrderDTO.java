package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class OrderDTO {

    private int id;

    private float weight;

    private int region;

    private Set<String> timePeriods = new HashSet<>();

    private Set<Integer> idOrders = new HashSet<>();

    private String datetimeAssign;

    private String datetimeComplete;

    private int courierId;

}
