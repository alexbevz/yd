package ru.bevz.yd.dto.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ContractDto {

    private int id;

    private float weight;

    private int region;

    private List<String> timePeriodList = new ArrayList<>();

    private List<Integer> idContractList = new ArrayList<>();

    private String datetimeAssign;

    private String datetimeComplete;

    private int courierId;

    private ValidAndNotValidIdLists validLists = new ValidAndNotValidIdLists();

}
