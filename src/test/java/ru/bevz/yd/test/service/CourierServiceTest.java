package ru.bevz.yd.test.service;


import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.bevz.yd.YandexDeliveryApplication;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.service.CourierService;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestWithException;
import ru.bevz.yd.test.service.annotation.CourierSqlGroup;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierWithException;

import static ru.bevz.yd.test.PathToTestDataConstant.CHANGELOG_MASTER_TEST_PATH;
import static ru.bevz.yd.test.PathToTestDataConstant.COURIER_SERVICE_PATH;

@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = YandexDeliveryApplication.class
)
@DisplayName("Тестирование класса CourierService")
public class CourierServiceTest {

    private final static String ADD_COURIER_TEST_NO_EXC =
            COURIER_SERVICE_PATH + "add-courier-no-exception.csv";

    private final static String ADD_COURIER_TEST_WITH_EXC =
            COURIER_SERVICE_PATH + "add-courier-with-exception.csv";

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

    @Rule
    public PreparedDbRule db =
            EmbeddedPostgresRules.preparedDatabase(
                    LiquibasePreparer.forClasspathLocation(CHANGELOG_MASTER_TEST_PATH));
    @Autowired
    private CourierService courierService;

    @ParameterizedTest
    @CsvFileSource(
            resources = {ADD_COURIER_TEST_NO_EXC},
            numLinesToSkip = 1
    )
    @DisplayName("Тестирование метода (без исключений): CourierService.addCourier")
    void addCourierTestNoException(@CSVToProvideDataForAddCourierTestNoException ProvideDataForAddCourierTestNoException courierDTOTest) {

        CourierDTO result = courierService.createCourier(courierDTOTest.getArgument());

        Assertions.assertEquals(courierDTOTest.getExpected(), result);
    }

    @ParameterizedTest
    @CsvFileSource(
            resources = {ADD_COURIER_TEST_WITH_EXC},
            numLinesToSkip = 1
    )
    @CourierSqlGroup
    @DisplayName("Тестирование метода (с исключениями): CourierService.addCourier")
    void addCourierTestWithException(@CSVToProvideDataForAddCourierTestWithException ProvideDataForAddCourierWithException courierDTOTest) {

        Assertions.assertThrows(
                courierDTOTest.getExpectedException(),
                () -> courierService.createCourier(courierDTOTest.getArgument())
        );
    }

    //TODO: implement test
    @ParameterizedTest
    @CsvFileSource(
            resources = "",
            numLinesToSkip = 1
    )
    void addCouriersTestNoException() {

    }

    //TODO: implement test
    @ParameterizedTest
    @CsvFileSource(
            resources = "",
            numLinesToSkip = 1
    )
    void addCouriersTestWithException() {

    }

}
