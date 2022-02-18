package ru.bevz.yd.test.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.postgres.embedded.LiquibasePreparer;
import io.zonky.test.db.postgres.junit.EmbeddedPostgresRules;
import io.zonky.test.db.postgres.junit.PreparedDbRule;
import io.zonky.test.db.postgres.junit.SingleInstancePostgresRule;
import lombok.SneakyThrows;
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
import ru.bevz.yd.controller.IdList;
import ru.bevz.yd.dto.model.CourierDTO;
import ru.bevz.yd.exception.NotValidObjectsException;
import ru.bevz.yd.service.CourierService;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.annotation.CSVToProvideDataForAddCourierTestWithException;
import ru.bevz.yd.test.service.annotation.CourierSqlGroup;
import ru.bevz.yd.test.service.pojo.DataForAddCouriersTestNoException;
import ru.bevz.yd.test.service.pojo.DataForAddCouriersTestWithException;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierTestNoException;
import ru.bevz.yd.test.service.pojo.ProvideDataForAddCourierWithException;

import java.io.File;
import java.lang.reflect.Type;
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

    @SneakyThrows
    //TODO: try optimize again...
    private static <T> T readFile(String path, TypeReference<T> reference) {
        ObjectMapper objectMapper = new JsonMapper();
        return objectMapper.readValue(
                new File(
                        CourierServiceTest.class.getClassLoader().getResource(path).getPath()
                ),
                reference
        );
    }

    private static Stream<Arguments> provideDataForAddCouriersTestNoException() {
        List<DataForAddCouriersTestNoException> data =
                readFile(
                        "courier-data/service/add-couriers-no-exception.json",
                        new TypeReference<>() {
                            @Override
                            public Type getType() {
                                return super.getType();
                            }
                        }
                );

        Stream<Arguments> arguments = Stream.of();
        for (DataForAddCouriersTestNoException dataCourier : data) {
            arguments = Stream.concat(
                    arguments,
                    Stream.of(Arguments.of(dataCourier.getExpected(), dataCourier.getArgument()))
            );
        }
        return arguments;
    }

    private static Stream<Arguments> provideDataForAddCouriersTestWithException() {
        List<DataForAddCouriersTestWithException> data =
                readFile(
                        "courier-data/service/add-couriers-with-exception.json",
                        new TypeReference<>() {
                            @Override
                            public Type getType() {
                                return super.getType();
                            }
                        });

        Stream<Arguments> arguments = Stream.of();
        for (DataForAddCouriersTestWithException dataCourier : data) {
            arguments = Stream.concat(
                    arguments,
                    Stream.of(Arguments.of(
                            new NotValidObjectsException()
                                    .setNameObjects("couriers")
                                    .setIdList(
                                            new IdList(dataCourier.getExpected().getIdCouriers())
                                    ),
                            dataCourier.getArgument()
                    ))
            );
        }
        return arguments;
    }

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

    @ParameterizedTest
    @MethodSource("provideDataForAddCouriersTestNoException")
    @CourierSqlGroup
    void addCouriersTestNoException(CourierDTO expected, Set<CourierDTO> courierDTOs) {

        CourierDTO result = courierService.createCouriers(courierDTOs);

        Assertions.assertEquals(expected.getIdCouriers(), result.getIdCouriers());
    }

    @ParameterizedTest
    @MethodSource("provideDataForAddCouriersTestWithException")
    @CourierSqlGroup
    void addCouriersTestWithException(NotValidObjectsException expected, Set<CourierDTO> courierDTOs) {

        try {
            courierService.createCouriers(courierDTOs);
            Assertions.fail("Not was caught some exceptions!");
        } catch (NotValidObjectsException result) {
            Assertions.assertEquals(expected, result);
        }

    }

}
