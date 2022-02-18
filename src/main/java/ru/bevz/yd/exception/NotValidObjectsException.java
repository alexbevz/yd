package ru.bevz.yd.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bevz.yd.controller.IdList;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class NotValidObjectsException extends RuntimeException {

    private String nameObjects;

    private IdList idList;

    public NotValidObjectsException(String nameObjects, Set<Integer> idList) {
        this.nameObjects = nameObjects;
        this.idList = new IdList(idList);
    }

}
