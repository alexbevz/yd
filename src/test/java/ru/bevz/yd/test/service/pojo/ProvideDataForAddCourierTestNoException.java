package ru.bevz.yd.test.service.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.dto.model.CourierDTO;

@Data
@Accessors(chain = true)
public class ProvideDataForAddCourierTestNoException {

    private CourierDTO expected;

    private CourierDTO argument;

}
