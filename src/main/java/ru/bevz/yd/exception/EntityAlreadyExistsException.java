package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.ENTITY_EXISTS_EXP;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Object entity) {
        super(ENTITY_EXISTS_EXP + entity.toString());
    }

}
