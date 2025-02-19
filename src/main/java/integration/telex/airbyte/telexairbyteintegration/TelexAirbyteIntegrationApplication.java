package integration.telex.airbyte.telexairbyteintegration;

import integration.telex.airbyte.telexairbyteintegration.util.HelperMethods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TelexAirbyteIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelexAirbyteIntegrationApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HelperMethods helperMethods() {
        return new HelperMethods();
    }

}
