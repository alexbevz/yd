package ru.bevz.yd.test.service.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bevz.yd.dto.model.CourierDTO;

import java.util.Set;

@Data
public class DataForAddCouriersTestNoException {
    @JsonProperty("expected")
    private @JsonIgnoreProperties(ignoreUnknown = true)
    CourierDTO expected;
    @JsonProperty("argument")
    private @JsonIgnoreProperties(ignoreUnknown = true) Set<CourierDTO> argument;
}