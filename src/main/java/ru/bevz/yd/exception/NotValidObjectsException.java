package ru.bevz.yd.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bevz.yd.controller.Id;
import ru.bevz.yd.controller.IdList;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class NotValidObjectsException extends RuntimeException {

    private String nameObjects;

    private IdList idList;

    public NotValidObjectsException(String nameObjects, List<Integer> idList) {
        this.nameObjects = nameObjects;
        this.idList = new IdList().setIdList(
                idList.stream()
                        .map(id -> new Id().setId(id))
                        .toList()
        );
    }

}
