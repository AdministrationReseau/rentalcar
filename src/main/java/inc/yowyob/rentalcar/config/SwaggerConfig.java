package inc.yowyob.rentalcar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Easy Rental Car API")
                .version("1.0")
                .description("API pour la gestion de location de véhicules")
                .termsOfService("https://www.easyrent.com/terms")
                .license(new License().name("Propriétaire").url("https://www.easyrent.com")));
    }
}
