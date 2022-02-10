package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.ORDER_DELIVERED_EXP;

public class OrderHasBeenDeliveredException extends RuntimeException {

    public OrderHasBeenDeliveredException(Object entity) {
        super(ORDER_DELIVERED_EXP + entity.toString());
    }

}
