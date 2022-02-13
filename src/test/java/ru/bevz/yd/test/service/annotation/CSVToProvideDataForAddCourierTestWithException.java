package ru.bevz.yd.test.service.annotation;

import org.junit.jupiter.params.aggregator.AggregateWith;
import ru.bevz.yd.test.service.aggregator.ProvideDataForAddCourierTestWithExceptionAggregator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(ProvideDataForAddCourierTestWithExceptionAggregator.class)
public @interface CSVToProvideDataForAddCourierTestWithException {
}
