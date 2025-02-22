package integration.telex.airbyte.telexairbyteintegration.util;

import integration.telex.airbyte.telexairbyteintegration.enums.SyncStatus;

import java.util.Map;

public class MessageFormatter {
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

    public static String formatMessageFromData(Map<String, Object> data) {
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
}
