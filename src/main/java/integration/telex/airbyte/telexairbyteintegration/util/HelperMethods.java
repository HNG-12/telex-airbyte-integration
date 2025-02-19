package integration.telex.airbyte.telexairbyteintegration.util;

import com.fasterxml.jackson.databind.JsonNode;

public class HelperMethods {

    public String getStringValue(JsonNode node, String... path) {
        JsonNode currentNode = node;
        for (String key : path) {
            currentNode = currentNode.get(key);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode.asText();
    }

    public boolean getBooleanValue(JsonNode node, String key) {
        JsonNode valueNode = node.get(key);

        if (valueNode.isMissingNode()) {
            return false;
        }
        return valueNode.asBoolean();
    }

    public int getIntValue(JsonNode node, String key) {
        JsonNode valueNode = node.path(key);

        if (valueNode.isMissingNode()) {
            return 0;
        }
        return valueNode.asInt();
    }
}
