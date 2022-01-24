package ru.bevz.yd.dto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidAndNotValidIdLists {

    private List<Integer> validIdList = new ArrayList<>();

    private List<Integer> notValidIdList = new ArrayList<>();

    public void addValidId(int id) {
        validIdList.add(id);
    }

    public void addNotValidId(int id) {
        notValidIdList.add(id);
    }

    public boolean hasNotValid() {
        return !notValidIdList.isEmpty();
    }
}
