package ru.bevz.yd.exception;

import static ru.bevz.yd.constant.GlobalConstant.CONVERSION_TP_EXP;

public class ConversionTPException extends RuntimeException {

    public ConversionTPException(Object object) {
        super(CONVERSION_TP_EXP + object.toString());
    }

}
