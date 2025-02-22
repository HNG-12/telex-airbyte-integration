package integration.telex.airbyte.telexairbyteintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.telex.airbyte.telexairbyteintegration.exception.PayloadProcessingException;
import integration.telex.airbyte.telexairbyteintegration.util.HelperMethods;
import integration.telex.airbyte.telexairbyteintegration.util.PayloadConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayloadProcessorService {
    private final ObjectMapper objectMapper;
    private final HelperMethods helperMethods;

    public PayloadProcessorService(ObjectMapper objectMapper, HelperMethods helperMethods) {
        this.objectMapper = objectMapper;
        this.helperMethods = helperMethods;
    }

    public Map<String, Object> extractDataFromPayload(String payloadData) {
        try {
            JsonNode payloadNode =  objectMapper.readTree(payloadData);
            validatePayload(payloadNode);

            JsonNode dataNode = payloadNode.path("data");

            Map<String, Object> data = new HashMap<>();
            data.put(PayloadConstants.WORKSPACE_NAME, helperMethods.getStringValue(dataNode,
                    PayloadConstants.WORKSPACE, PayloadConstants.NAME));
            data.put(PayloadConstants.CONNECTION_NAME, helperMethods.getStringValue(dataNode, PayloadConstants.CONNECTION, PayloadConstants.NAME));
            data.put(PayloadConstants.SOURCE_NAME, helperMethods.getStringValue(dataNode, PayloadConstants.SOURCE, PayloadConstants.NAME));
            data.put(PayloadConstants.DESTINATION_NAME, helperMethods.getStringValue(dataNode, PayloadConstants.DESTINATION, PayloadConstants.NAME));
            data.put(PayloadConstants.CONNECTION_URL, helperMethods.getStringValue(dataNode, PayloadConstants.CONNECTION, PayloadConstants.URL));
            data.put(PayloadConstants.SOURCE_URL, helperMethods.getStringValue(dataNode, PayloadConstants.SOURCE, PayloadConstants.URL));
            data.put(PayloadConstants.DESTINATION_URL, helperMethods.getStringValue(dataNode, PayloadConstants.DESTINATION, PayloadConstants.URL));
            data.put(PayloadConstants.SUCCESSFUL_SYNC, helperMethods.getBooleanValue(dataNode, PayloadConstants.SUCCESS));
            data.put(PayloadConstants.DURATION_FORMATTED_KEY, helperMethods.getStringValue(dataNode, PayloadConstants.DURATION_FORMATTED));
            data.put(PayloadConstants.RECORDS_EMITTED_KEY, helperMethods.getIntValue(dataNode, PayloadConstants.RECORDS_EMITTED));
            data.put(PayloadConstants.RECORDS_COMMITTED_KEY, helperMethods.getIntValue(dataNode, PayloadConstants.RECORDS_COMMITTED));
            data.put(PayloadConstants.BYTES_EMITTED_FORMATTED_KEY, helperMethods.getStringValue(dataNode, PayloadConstants.BYTES_EMITTED_FORMATTED));
            data.put(PayloadConstants.BYTES_COMMITTED_FORMATTED_KEY, helperMethods.getStringValue(dataNode, PayloadConstants.BYTES_COMMITTED_FORMATTED));


            if (!(Boolean) data.get(PayloadConstants.SUCCESSFUL_SYNC)) {
                data.put(PayloadConstants.ERROR_MESSAGE_KEY, helperMethods.getStringValue(dataNode,
                        PayloadConstants.ERROR_MESSAGE));
            }

            return data;

        } catch (Exception e) {
            throw new PayloadProcessingException("Error occurred while extracting data from payload", e);
        }
    }

    private void validatePayload(JsonNode payloadNode) {
        if (payloadNode == null || payloadNode.path(PayloadConstants.DATA).isMissingNode()) {
            throw new IllegalArgumentException("Invalid Payload: Payload data is missing");
        }
    }
}
