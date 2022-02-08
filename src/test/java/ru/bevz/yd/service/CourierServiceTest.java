package ru.bevz.yd.service;


import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.bevz.yd.YandexDeliveryApplication;

import java.util.stream.Stream;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;


@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES
)
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = MOCK,
        classes = YandexDeliveryApplication.class
)
@AutoConfigureMockMvc
//TODO: ask some info about how use EmbeddedDatabase
public class CourierServiceTest {

    @Autowired
    private CourierService courierService;

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

    //TODO: add test-changelog that linking with master changelog and add sql with data for courier
    @Rule
    public PreparedDbRule db =
            EmbeddedPostgresRules.preparedDatabase(
                    LiquibasePreparer.forClasspathLocation("/db/changelog/db.changelog-master.xml"));

    private static Stream<Arguments> provideDataForGetEarningsCourierTest() {
        return Stream.of(
                Arguments.of(0, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetEarningsCourierTest")
    public void getEarningsCourierTest(float expected, int courierId) {

        float result = courierService.getEarningsCourier(courierId);

        Assertions.assertEquals(expected, result);
    }
}
