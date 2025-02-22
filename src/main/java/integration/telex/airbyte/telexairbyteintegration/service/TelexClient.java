package integration.telex.airbyte.telexairbyteintegration.service;

import integration.telex.airbyte.telexairbyteintegration.exception.TelexCommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelexClient {
    private static final Logger LOG = LoggerFactory.getLogger(TelexService.class);

    private final RestTemplate restTemplate;
    private final WebhookService webhookService;

    public TelexClient(RestTemplate restTemplate, WebhookService webhookService) {
        this.restTemplate = restTemplate;
        this.webhookService = webhookService;
    }

    public void sendToTelexChannel(String message) {
        try {
            String telexWebhookUrl = webhookService.getTelexWebhookUrl();
            restTemplate.postForEntity(telexWebhookUrl, message, String.class);
            LOG.info("Successfully sent message to Telex channel with message");
        } catch (Exception e) {
            LOG.error("Error occurred while sending message to Telex channel", e);
            throw new TelexCommunicationException("Error occurred while sending message to Telex channel", e);
        }
    }
}
