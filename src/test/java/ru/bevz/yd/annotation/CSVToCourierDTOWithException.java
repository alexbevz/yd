package ru.bevz.yd.annotation;

import org.junit.jupiter.params.aggregator.AggregateWith;
import ru.bevz.yd.aggregator.CourierDTOForCSVNoExceptionAggregator;
import ru.bevz.yd.aggregator.CourierDTOForCSVWithExceptionAggregator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(CourierDTOForCSVWithExceptionAggregator.class)
public @interface CSVToCourierDTOWithException {
}
