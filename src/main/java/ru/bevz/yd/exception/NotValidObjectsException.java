package ru.bevz.yd.exception;

import lombok.Data;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class NotValidObjectsException extends RuntimeException {

    private String nameObjects;

    private IdList idList;

    public NotValidObjectsException(String nameObjects, List<Integer> idList) {
        this.nameObjects = nameObjects;
        this.idList = new IdList().setIdList(
                idList.stream()
                        .map(id -> new Id().setId(id))
                        .collect(Collectors.toSet())
        );
    }

}
