package ru.bevz.yd.annotation;

import org.junit.jupiter.params.aggregator.AggregateWith;
import ru.bevz.yd.aggregator.CourierDTONoExceptionAggregator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(CourierDTONoExceptionAggregator.class)
public @interface CSVToCourierDTONoException {
}
