package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.ENTITY_NOT_EXISTS_EXP;

public class EntityNotExistsException extends RuntimeException {

    public EntityNotExistsException(Object entity) {
        super(ENTITY_NOT_EXISTS_EXP + entity.toString());
    }

}
