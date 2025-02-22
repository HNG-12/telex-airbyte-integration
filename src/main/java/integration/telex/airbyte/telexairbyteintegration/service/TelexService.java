package integration.telex.airbyte.telexairbyteintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import integration.telex.airbyte.telexairbyteintegration.exception.PayloadProcessingException;
import integration.telex.airbyte.telexairbyteintegration.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelexService {
    private static final Logger LOG = LoggerFactory.getLogger(TelexService.class);
    private final ObjectMapper objectMapper;
    private final PayloadProcessorService payloadProcessorService;
    private final TelexClient telexClient;


    public TelexService(ObjectMapper objectMapper, PayloadProcessorService payloadProcessorService,
                        TelexClient telexClient) {
        this.objectMapper = objectMapper;
        this.payloadProcessorService = payloadProcessorService;
        this.telexClient = telexClient;
    }

    public void processPayload(String payloadData) {
        try {
            if (payloadData == null || payloadData.isEmpty()) {
                throw new IllegalArgumentException("Payload data cannot be null or empty");
            }

            Map<String, Object> data = payloadProcessorService.extractDataFromPayload(payloadData);
            String message = MessageFormatter.formatMessageFromData(data);

            Map<String, Object> telexPayload = new HashMap<>();

            boolean isSuccessfulSync = (Boolean) data.get("successful_sync");

            telexPayload.put("event_name", data.get("connection_name"));
            telexPayload.put("username", "airbyte");
            telexPayload.put("status", isSuccessfulSync ? "success" : "failed");
            telexPayload.put("message", message);

            telexClient.sendToTelexChannel(objectMapper.writeValueAsString(telexPayload));
            LOG.info("Successfully sent message to Telex channel");
        } catch (Exception e) {
            throw new PayloadProcessingException("Error occurred while processing payload", e);
        }
    }


}
