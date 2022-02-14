package ru.bevz.yd.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class OrderInfo {

    @JsonProperty("order_id")
    @Schema(
            required = true,
            description = "Индетификатор заказа",
            example = "25",
            type = "integer"
    )
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private int id;

    @JsonProperty("weight")
    @Schema(
            required = true,
            description = "Масса заказа",
            example = "0.5",
            type = "float"
    )
    @DecimalMin("0.01")
    @DecimalMax("50.0")
    private float weight;

    @JsonProperty("region")
    @Schema(
            required = true,
            description = "Номер района заказа",
            example = "12",
            type = "integer"
    )
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private int region;

    @JsonProperty("delivery_hours")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @ArraySchema(
            arraySchema = @Schema(
                    required = true,
                    description = "Часы доставки"
            ),
            schema = @Schema(
                    required = true,
                    description = "Промежуток времени",
                    example = "20:00-15:25",
                    type = "string"
            )
    )
    @NotEmpty
    private List<String> deliveryHours;

}
