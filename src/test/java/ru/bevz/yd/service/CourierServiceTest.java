package ru.bevz.yd.service;


import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.bevz.yd.YandexDeliveryApplication;

import java.util.stream.Stream;


@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES
)
@Sql({"/test-db/test-courier-service-data.sql"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = YandexDeliveryApplication.class
)
@AutoConfigureMockMvc
public class CourierServiceTest {

    @Autowired
    private CourierService courierService;

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

    @Rule
    public PreparedDbRule db =
            EmbeddedPostgresRules.preparedDatabase(
                    LiquibasePreparer.forClasspathLocation("/test-db/changelog-master-test.xml"));

    private static Stream<Arguments> provideDataForGetEarningsCourierTestWithoutExceptions() {
        return Stream.of(
                Arguments.of(0, 1),
                Arguments.of(0, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetEarningsCourierTestWithoutExceptions")
    public void getEarningsCourierTestWithoutExceptions(float expected, int courierId) {

        float result = courierService.getEarningsCourier(courierId);

        Assertions.assertEquals(expected, result);
    }
}
