package ru.bevz.yd.aggregator;


import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.pojo.CourierDTOForCSVNoException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CourierDTONoExceptionAggregator implements ArgumentsAggregator {

    @Override
    public CourierDTOForCSVNoException aggregateArguments(
            ArgumentsAccessor arguments,
            ParameterContext context
    ) throws ArgumentsAggregationException {
        return new CourierDTOForCSVNoException()
                .setExpected(
                        new CourierDTO()
                                .setId(arguments.getInteger(0))
                                .setType(arguments.getString(1))
                                .setRegions(
                                        Arrays.stream(arguments.getString(2).split(" "))
                                                .map(Integer::parseInt)
                                                .collect(Collectors.toSet())
                                )
                                .setTimePeriods(
                                        Arrays.stream(arguments.getString(3).split(" "))
                                                .collect(Collectors.toSet())
                                )
                )
                .setArgument(
                        new CourierDTO()
                                .setId(arguments.getInteger(4))
                                .setType(arguments.getString(5))
                                .setRegions(
                                        Arrays.stream(arguments.getString(6).split(" "))
                                                .map(Integer::parseInt)
                                                .collect(Collectors.toSet())
                                )
                                .setTimePeriods(
                                        Arrays.stream(arguments.getString(7).split(" "))
                                                .collect(Collectors.toSet())
                                )
                );
    }

}
