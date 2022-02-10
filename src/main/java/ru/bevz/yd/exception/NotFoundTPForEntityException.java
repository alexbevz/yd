package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.NOT_FOUND_TP_FOR_ENTITY_EXP;

public class NotFoundTPForEntityException extends RuntimeException {

    public NotFoundTPForEntityException(Object entity) {
        super(NOT_FOUND_TP_FOR_ENTITY_EXP + entity.toString());
    }

}
