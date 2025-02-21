package integration.telex.airbyte.telexairbyteintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.telex.airbyte.telexairbyteintegration.enums.SyncStatus;
import integration.telex.airbyte.telexairbyteintegration.exception.PayloadProcessingException;
import integration.telex.airbyte.telexairbyteintegration.exception.TelexCommunicationException;
import integration.telex.airbyte.telexairbyteintegration.util.HelperMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelexService {
    private static final Logger LOG = LoggerFactory.getLogger(TelexService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HelperMethods helperMethods;
    private String telexWebhookUrl;
    @Value("${integrations.json.url}")
    private String integrationsJsonUrl;

    private static final String AIRBYTE_MESSAGE_TEMPLATE =
            """
                    %s Sync %s
                    **Connection:** [%s](%s)
                    **Source:** [%s](%s)
                    **Destination:** [%s](%s)
                    **Duration:** %s
                    **Records Emitted:** %d
                    **Records Committed:** %d
                    **Bytes Emitted:** %s
                    **Bytes Committed:** %s""";

    public TelexService(RestTemplate restTemplate, ObjectMapper objectMapper, HelperMethods helperMethods) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.helperMethods = helperMethods;
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

    private Map<String, Object> extractDataFromPayload(String payloadData) {
        try {
            JsonNode payloadNode =  objectMapper.readTree(payloadData);
            validatePayload(payloadNode);

            JsonNode dataNode = payloadNode.path("data");

            Map<String, Object> data = new HashMap<>();
            data.put("workspace_name", helperMethods.getStringValue(dataNode, "workspace", "name"));
            data.put("connection_name", helperMethods.getStringValue(dataNode, "connection", "name"));
            data.put("source_name", helperMethods.getStringValue(dataNode, "source", "name"));
            data.put("destination_name", helperMethods.getStringValue(dataNode, "destination", "name"));
            data.put("connection_url", helperMethods.getStringValue(dataNode, "connection", "url"));
            data.put("source_url", helperMethods.getStringValue(dataNode, "source", "url"));
            data.put("destination_url", helperMethods.getStringValue(dataNode, "destination", "url"));
            data.put("successful_sync", helperMethods.getBooleanValue(dataNode, "success"));
            data.put("duration_formatted", helperMethods.getStringValue(dataNode, "durationFormatted"));
            data.put("records_emitted", helperMethods.getIntValue(dataNode, "recordsEmitted"));
            data.put("records_committed", helperMethods.getIntValue(dataNode, "recordsCommitted"));
            data.put("bytes_emitted_formatted", helperMethods.getStringValue(dataNode, "bytesEmittedFormatted"));
            data.put("bytes_committed_formatted", helperMethods.getStringValue(dataNode, "bytesCommittedFormatted"));

            if (!(Boolean) data.get("successful_sync")) {
                data.put("error_message", helperMethods.getStringValue(dataNode, "errorMessage"));
            }

            return data;

        } catch (Exception e) {
            throw new PayloadProcessingException("Error occurred while extracting data from payload", e);
        }
    }

    private String formatMessageFromData(Map<String, Object> data) {
//        String syncStatusEmoji = (Boolean) data.get("successful_sync") ? ":green_circle:" : ":red_circle:";
//        String status = (Boolean) data.get("successful_sync") ? "succeeded" : "failed";
        SyncStatus syncStatus = (Boolean) data.get("successful_sync") ? SyncStatus.SUCCESS : SyncStatus.FAILED;

        String content = String.format(AIRBYTE_MESSAGE_TEMPLATE,
                syncStatus.getEmoji(),
                syncStatus.getDescription(),
                data.get("connection_name"),
                data.get("connection_url"),
                data.get("source_name"),
                data.get("source_url"),
                data.get("destination_name"),
                data.get("destination_url"),
                data.get("duration_formatted"),
                data.get("records_emitted"),
                data.get("records_committed"),
                data.get("bytes_emitted_formatted"),
                data.get("bytes_committed_formatted"));

        if (syncStatus == SyncStatus.FAILED) {
            content += "\n\n**Error Message:** " + data.get("error_message");
        }

        return content;
    }

    private void sendToTelexChannel(String message) {
        try {
            initializeWebhookUrl();
            restTemplate.postForEntity(telexWebhookUrl, message, String.class);
        } catch (Exception e) {
            throw new TelexCommunicationException("Error occurred while sending message to Telex channel", e);
        }
    }

    public void processPayload(String payloadData) {
        try {
            if (payloadData == null || payloadData.isEmpty()) {
                throw new IllegalArgumentException("Payload data cannot be null or empty");
            }

            Map<String, Object> data = extractDataFromPayload(payloadData);

            Map<String, Object> telexPayload = new HashMap<>();

            boolean isSuccessfulSync = (Boolean) data.get("successful_sync");

            telexPayload.put("event_name", data.get("connection_name"));
            telexPayload.put("username", "airbyte");
            telexPayload.put("status", isSuccessfulSync ? "success" : "failed");
            telexPayload.put("message", formatMessageFromData(data));

            sendToTelexChannel(objectMapper.writeValueAsString(telexPayload));
        } catch (Exception e) {
            throw new PayloadProcessingException("Error occurred while processing payload", e);
        }
    }

    private void validatePayload(JsonNode payloadNode) {
        if (payloadNode == null || payloadNode.path("data").isMissingNode()) {
            throw new IllegalArgumentException("Invalid Payload: Payload data is missing");
        }
    }
}
