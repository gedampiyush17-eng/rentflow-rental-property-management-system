package com.rentflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rentFlowOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("RentFlow API")
                        .version("1.0")
                        .description("Rental and Property Management REST API")
                        .contact(new Contact()
                                .name("Piyush Gedam")
                                .email("gedampiyush17@gmail.com")));
    }
}