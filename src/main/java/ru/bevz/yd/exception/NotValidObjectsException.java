package ru.bevz.yd.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.bevz.yd.controller.IdList;


@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class NotValidObjectsException extends RuntimeException {

    private String nameObjects;

    private IdList idList;

}
