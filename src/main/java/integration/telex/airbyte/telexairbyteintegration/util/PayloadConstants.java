package integration.telex.airbyte.telexairbyteintegration.util;

public final class PayloadConstants {
    private PayloadConstants() {
    }

    // JSON Keys
    public static final String DATA = "data";
    public static final String WORKSPACE = "workspace";
    public static final String CONNECTION = "connection";
    public static final String SOURCE = "source";
    public static final String DESTINATION = "destination";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String SUCCESS = "success";
    public static final String DURATION_FORMATTED = "durationFormatted";
    public static final String RECORDS_EMITTED = "recordsEmitted";
    public static final String RECORDS_COMMITTED = "recordsCommitted";
    public static final String BYTES_EMITTED_FORMATTED = "bytesEmittedFormatted";
    public static final String BYTES_COMMITTED_FORMATTED = "bytesCommittedFormatted";
    public static final String ERROR_MESSAGE = "errorMessage";

    // Data Map Keys
    public static final String WORKSPACE_NAME = "workspace_name";
    public static final String CONNECTION_NAME = "connection_name";
    public static final String SOURCE_NAME = "source_name";
    public static final String DESTINATION_NAME = "destination_name";
    public static final String CONNECTION_URL = "connection_url";
    public static final String SOURCE_URL = "source_url";
    public static final String DESTINATION_URL = "destination_url";
    public static final String SUCCESSFUL_SYNC = "successful_sync";
    public static final String DURATION_FORMATTED_KEY = "duration_formatted";
    public static final String RECORDS_EMITTED_KEY = "records_emitted";
    public static final String RECORDS_COMMITTED_KEY = "records_committed";
    public static final String BYTES_EMITTED_FORMATTED_KEY = "bytes_emitted_formatted";
    public static final String BYTES_COMMITTED_FORMATTED_KEY = "bytes_committed_formatted";
    public static final String ERROR_MESSAGE_KEY = "error_message";
}
