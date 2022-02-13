package ru.bevz.yd.test.service.aggregator;


import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierWithException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ProvideDataForAddCourierTestWithExceptionAggregator implements ArgumentsAggregator {

    @SneakyThrows
    @Override
    public ProvideDataForAddCourierWithException aggregateArguments(
            ArgumentsAccessor arguments,
            ParameterContext context
    ) throws ArgumentsAggregationException {
        return new ProvideDataForAddCourierWithException()
                .setExpectedException(
                        (Class<Throwable>) Class.forName(
                                arguments.getString(0)
                        )
                )
                .setArgument(
                        new CourierDTO()
                                .setId(arguments.getInteger(1))
                                .setType(arguments.getString(2).equals("null") ? null : arguments.getString(2))
                                .setRegions(arguments.getString(3).equals("nullList") ? null :
                                        Arrays.stream(arguments.getString(3).split(" "))
                                                .map(val -> val.equals("null") ? 0 : Integer.parseInt(val))
                                                .collect(Collectors.toSet())
                                )
                                .setTimePeriods(arguments.getString(4).equals("nullList") ? null :
                                        Arrays.stream(arguments.getString(4).split(" "))
                                                .collect(Collectors.toSet())
                                )
                );
    }

}
