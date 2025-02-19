package integration.telex.airbyte.telexairbyteintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.telex.airbyte.telexairbyteintegration.exception.PayloadProcessingException;
import integration.telex.airbyte.telexairbyteintegration.exception.TelexCommunicationException;
import integration.telex.airbyte.telexairbyteintegration.util.HelperMethods;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelexService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HelperMethods helperMethods;
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
                    **Bytes Committed:** %s
                    """;

    public TelexService(RestTemplate restTemplate, ObjectMapper objectMapper, HelperMethods helperMethods) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.helperMethods = helperMethods;
    }

    private Map<String, Object> extractDataFromPayload(String payloadData) {
        try {
            JsonNode payloadNode =  objectMapper.readTree(payloadData);

            JsonNode dataNode = payloadNode.path("data");
            System.out.println(dataNode);
            validatePayload(dataNode);

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
        String syncStatusEmoji = (Boolean) data.get("successful_sync") ? ":green_circle:" : ":red_circle:";
        String status = (Boolean) data.get("successful_sync") ? "succeeded" : "failed";

        String content = String.format(AIRBYTE_MESSAGE_TEMPLATE,
                syncStatusEmoji,
                status,
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

        if (!(Boolean) data.get("successful_sync")) {
            content += "\n\n**Error Message:** " + data.get("error_message");
        }

        return content;
    }

    private void sendToTelexChannel(String message) {
        try {
            String telexWebhookUrl = "https://ping.telex.im/v1/webhooks/0195135b-5f5f-76a7-b23a-8251952c5b42";
            restTemplate.postForEntity(telexWebhookUrl, message, String.class);
        } catch (Exception e) {
            throw new TelexCommunicationException("Error occurred while sending message to Telex channel", e);
        }
    }

    public void processPayload(String payloadData) {
        if (payloadData == null || payloadData.isEmpty()) {
            throw new IllegalArgumentException("Payload data cannot be null or empty");
        }

        Map<String, Object> data = extractDataFromPayload(payloadData);
        String message = formatMessageFromData(data);

        sendToTelexChannel(message);
    }

    private void validatePayload(JsonNode payloadNode) {
        if (payloadNode == null || payloadNode.path("data").isMissingNode()) {
            throw new IllegalArgumentException("Invalid Payload: Payload data is missing");
        }
    }
}
