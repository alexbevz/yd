package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.ORDER_ASSIGNED_EXP;

public class OrderAssignedForOtherCourierException extends RuntimeException {

    public OrderAssignedForOtherCourierException(Object entity) {
        super(ORDER_ASSIGNED_EXP + entity.toString());
    }

}
