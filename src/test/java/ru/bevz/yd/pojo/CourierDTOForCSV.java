package ru.bevz.yd.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.bevz.yd.dto.model.CourierDTO;

@Data
@Accessors(chain = true)
public class CourierDTOForCSV {

    private CourierDTO expected;

    private CourierDTO argument;

}
