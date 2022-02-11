package ru.bevz.yd.aggregator;


import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.pojo.CourierDTOForCSVWithException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CourierDTOForCSVWithExceptionAggregator implements ArgumentsAggregator {

    @SneakyThrows
    @Override
    public CourierDTOForCSVWithException aggregateArguments(
            ArgumentsAccessor arguments,
            ParameterContext context
    ) throws ArgumentsAggregationException {
        return new CourierDTOForCSVWithException()
                .setExpectedException(
                        (Class<Throwable>) Class.forName(
                                arguments.getString(0)
                        )
                )
                .setArgument(
                        new CourierDTO()
                                .setId(arguments.getInteger(1))
                                .setType(arguments.getString(2))
                                .setRegions(
                                        Arrays.stream(arguments.getString(3).split(" "))
                                                .map(Integer::parseInt)
                                                .collect(Collectors.toSet())
                                )
                                .setTimePeriods(
                                        Arrays.stream(arguments.getString(4).split(" "))
                                                .collect(Collectors.toSet())
                                )
                );
    }

}
