package am.mmtobacco.mm_tobacco_application.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MM Tobacco API",
                version = "1.0",
                description = "Documentation API for MM Tobacco project"
        )
)
public class SwaggerConfig {
}

