package ru.bevz.yd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Yandex Delivery")
                        .version("1.0.0")
                        .contact(
                                new Contact()
                                        .email("as-bivz@yandex.ru")
                                        .url("https://github.com/alexbevz")
                                        .name("Bevz Aleksandr")
                        )
                        .description("")
                        .license(
                                new License()
                                        .name("ITBevz")
                                        .url("https://itbevz.ru/")
                        )
        );
    }

}
