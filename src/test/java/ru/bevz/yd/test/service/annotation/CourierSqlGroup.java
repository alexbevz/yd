package ru.bevz.yd.test.service.annotation;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.bevz.yd.test.PathToTestDataConstant.COURIER_DELETE_DATA_PATH;
import static ru.bevz.yd.test.PathToTestDataConstant.COURIER_INIT_DATA_PATH;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SqlGroup(
        value = {
                @Sql(
                        value = {
                                COURIER_DELETE_DATA_PATH,
                                COURIER_INIT_DATA_PATH
                        },
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
                ),
                @Sql(
                        value = COURIER_DELETE_DATA_PATH,
                        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
                )
        }
)
public @interface CourierSqlGroup {
}
