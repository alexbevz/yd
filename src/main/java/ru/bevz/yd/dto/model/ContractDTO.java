package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ContractDTO {

    private int id;

    private float weight;

    private int region;

    private Set<String> timePeriods = new HashSet<>();

    private List<Integer> idContracts = new ArrayList<>();

    private String datetimeAssign;

    private String datetimeComplete;

    private int courierId;

}
