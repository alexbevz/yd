package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ContractDto {

    private int id;

    private float weight;

    private int region;

    private List<String> timePeriodList;

    private String datetimeAssign;

    private String datetimeComplete;

    private int courierId;

    private ValidAndNotValidIdLists validLists;

}
