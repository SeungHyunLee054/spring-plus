package org.example.expert.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(private val objectMapper: ObjectMapper) {

    @PostConstruct
    fun init() {
        ModelConverters.getInstance().addConverter(ModelResolver(objectMapper))
    }

    @Bean
    fun openApi(): OpenAPI = OpenAPI().apply {
        addSecurityItem(
            SecurityRequirement()
                .addList("bearer-key")
                .addList("Refresh-Token")
        )
        components = components()
        info = apiInfo()
    }

    private fun components(): Components = Components().apply {
        addSecuritySchemes(
            "bearer-key",
            SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        )
        addSecuritySchemes(
            "Refresh-Token",
            SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .`in`(SecurityScheme.In.HEADER)
                .name("Refresh-Token")
                .description("Bearer 를 붙여서 넣어주어야 합니다.")
        )
    }

    private fun apiInfo(): Info = Info().apply {
        title("Spring-Plus")
        description("Spring-Plus 개인 프로젝트")
        version("1.0")
    }
}
