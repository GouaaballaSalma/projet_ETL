package ma.cfgbank.lcn_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API Plateforme LCN - CFG Bank",
        description = "API de gestion des incidents de paiement LCN (Consultation et Ajout manuel)",
        version = "1.0"
    )
)
public class OpenApiConfig {
}
