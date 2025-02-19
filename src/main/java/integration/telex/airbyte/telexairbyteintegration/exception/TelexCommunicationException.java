package integration.telex.airbyte.telexairbyteintegration.exception;

public class TelexCommunicationException extends RuntimeException {
    public TelexCommunicationException(String message) {
        super(message);
    }

    public TelexCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
