package integration.telex.airbyte.telexairbyteintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${integrations.json.url}")
    private String integrationsJsonUrl;
    private String telexWebhookUrl;

    public WebhookService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private void initializeWebhookUrl() {
        try {
            assert integrationsJsonUrl != null;
            String jsonString = restTemplate.getForObject(integrationsJsonUrl, String.class);
            if (jsonString == null) {
                throw new IllegalStateException("Invalid JSON url");
            }

            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode settingsNode = rootNode.path("data").path("settings");

            if (settingsNode.isArray()) {
                for (JsonNode setting : settingsNode) {
                    String label = setting.path("label").asText();
                    if ("webhook_url".equals(label)) {
                        this.telexWebhookUrl = setting.path("default").asText();
                        break;
                    }
                }
            }

            if (this.telexWebhookUrl == null || this.telexWebhookUrl.isEmpty()) {
                throw new IllegalStateException("webhook_url not found in integrations.json");
            }

            LOG.info("Successfully loaded webhook URL: {}", this.telexWebhookUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize TelexService due to error fetching or parsing integrations.json", e);
        }
    }

    public String getTelexWebhookUrl() {
        if (telexWebhookUrl == null) {
            initializeWebhookUrl();
        }
        return telexWebhookUrl;
    }
}
