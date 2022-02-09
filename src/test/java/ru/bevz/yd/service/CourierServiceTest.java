package ru.bevz.yd.service;


import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.bevz.yd.YandexDeliveryApplication;
import ru.bevz.yd.annotation.CSVToCourierDTOForCSV;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.pojo.CourierDTOForCSV;


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

    @ParameterizedTest
    @CsvFileSource(
            resources = {"/test-db/data-courier-test/add-new-courier-test.csv"},
            numLinesToSkip = 1
    )
    public void addNewCourierTestNoExceptions(@CSVToCourierDTOForCSV CourierDTOForCSV courierDTOForCsv) {

        CourierDTO result;

        try {
            result = courierService.addNewCourier(courierDTOForCsv.getArgument());
            Assertions.assertEquals(courierDTOForCsv.getExpected(), result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
