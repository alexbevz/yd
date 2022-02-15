package ru.bevz.yd.test.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.bevz.yd.YandexDeliveryApplication;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.service.CourierService;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestWithException;
import ru.bevz.yd.test.service.annotation.CourierSqlGroup;
import ru.bevz.yd.test.service.pojo.DataForAddCouriersTestNoException;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierWithException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
                    LiquibasePreparer.forClasspathLocation(CHANGELOG_MASTER_TEST_PATH)
            );

    @Autowired
    private CourierService courierService;

    @ParameterizedTest
    @CsvFileSource(
            resources = {ADD_COURIER_TEST_NO_EXC},
            numLinesToSkip = 1
    )
    @DisplayName("Тестирование метода (без исключений): CourierService.addCourier")
    void addCourierTestNoException(
            @CSVToProvideDataForAddCourierTestNoException ProvideDataForAddCourierTestNoException courierDTOTest
    ) {

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
    void addCourierTestWithException(
            @CSVToProvideDataForAddCourierTestWithException ProvideDataForAddCourierWithException courierDTOTest
    ) {

        Assertions.assertThrows(
                courierDTOTest.getExpectedException(),
                () -> courierService.createCourier(courierDTOTest.getArgument())
        );
    }

    //TODO: need to optimize
    private static Stream<Arguments> provideDataForAddCouriersTestNoException() {
        Stream<Arguments> arguments = Stream.of();
        ObjectMapper objectMapper = new JsonMapper();
        List<DataForAddCouriersTestNoException> data = new ArrayList<>();
        try {
            URL url = CourierServiceTest.class.getClassLoader()
                    .getResource("courier-data/service/add-couriers-no-exception.json");
            assert url != null;
            data = objectMapper.readValue(
                    new File(url.getPath()),
                    new TypeReference<>() {
                        @Override
                        public Type getType() {
                            return super.getType();
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (DataForAddCouriersTestNoException dataCourier : data) {
            arguments = Stream.concat(
                    arguments,
                    Stream.of(Arguments.of(dataCourier.getExpected(), dataCourier.getArgument()))
            );
        }
        return arguments;
    }

    @ParameterizedTest
    @MethodSource("provideDataForAddCouriersTestNoException")
    void addCouriersTestNoException(CourierDTO expected, Set<CourierDTO> courierDTOs) {

        CourierDTO result = courierService.createCouriers(courierDTOs.stream().toList());

        Assertions.assertEquals(
                new HashSet<>(expected.getIdCouriers()),
                new HashSet<>(result.getIdCouriers())
        );
    }

}
