package com.google.spanner.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.view.RedirectView
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    Docket productApi() {
        new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex('/v1/.*'))
                .build()
                .directModelSubstitute(RedirectView, Void)
                .directModelSubstitute(ResponseEntity, Void)
                .apiInfo(metaData())
    }

    private static ApiInfo metaData() {
        new ApiInfoBuilder()
                .title('Google Spanner REST API')
                .description('Spanner REST API for Big Id')
                .version('1.0.0')
                .license('Apache License Version 2.0')
                .licenseUrl('https://www.apache.org/licenses/LICENSE-2.0\'')
                .contact(new Contact('Aakash Narla', '', 'aakash.narla.rao@gmail.com'))
                .build()
    }

}
