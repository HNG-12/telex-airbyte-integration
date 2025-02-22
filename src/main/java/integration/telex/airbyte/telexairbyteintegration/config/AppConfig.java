package integration.telex.airbyte.telexairbyteintegration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import integration.telex.airbyte.telexairbyteintegration.service.PayloadProcessorService;
import integration.telex.airbyte.telexairbyteintegration.util.HelperMethods;
import integration.telex.airbyte.telexairbyteintegration.util.MessageFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HelperMethods helperMethods() {
        return new HelperMethods();
    }

}
