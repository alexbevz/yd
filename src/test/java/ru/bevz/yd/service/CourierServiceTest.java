package ru.bevz.yd.service;


import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.bevz.yd.YandexDeliveryApplication;
import ru.bevz.yd.annotation.CSVToCourierDTONoException;
import ru.bevz.yd.annotation.CSVToCourierDTOWithException;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.pojo.CourierDTOForCSVNoException;
import ru.bevz.yd.pojo.CourierDTOForCSVWithException;

@AutoConfigureEmbeddedDatabase(
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = YandexDeliveryApplication.class
)
public class CourierServiceTest {

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();
    @Rule
    public PreparedDbRule db =
            EmbeddedPostgresRules.preparedDatabase(
                    LiquibasePreparer.forClasspathLocation("/changelog-master-test.xml"));
    @Autowired
    private CourierService courierService;

    @ParameterizedTest
    @CsvFileSource(
            resources = {"/courier-test-data/add-new-courier-test-no-exception.csv"},
            numLinesToSkip = 1
    )
    public void addNewCourierTestNoException(@CSVToCourierDTONoException CourierDTOForCSVNoException courierDTOTest) {

        CourierDTO result = courierService.addNewCourier(courierDTOTest.getArgument());

        Assertions.assertEquals(courierDTOTest.getExpected(), result);
    }

    @ParameterizedTest
    @CsvFileSource(
            resources = {"/courier-test-data/add-new-courier-test-with-exception.csv"},
            numLinesToSkip = 1,
            emptyValue = "null",
            nullValues = "nullList"
    )
    @Sql(
            value = {
                    "/courier-test-data/courier-service-test-delete-data.sql",
                    "/courier-test-data/courier-service-test-insert-data.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            value = "/courier-test-data/courier-service-test-delete-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void addNewCourierTestWithException(@CSVToCourierDTOWithException CourierDTOForCSVWithException courierDTOTest) {

        Assertions.assertThrows(
                courierDTOTest.getExpectedException(),
                () -> courierService.addNewCourier(courierDTOTest.getArgument())
        );
    }

}
