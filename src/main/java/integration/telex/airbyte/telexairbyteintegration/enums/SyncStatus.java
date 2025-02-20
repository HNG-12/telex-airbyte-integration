package integration.telex.airbyte.telexairbyteintegration.enums;

public enum SyncStatus {
    SUCCESS(":green_circle:", "succeeded", "success"),
    FAILED(":red_circle:", "failed", "failed");

    private final String emoji;
    private final String description;
    private final String telexStatus;

    SyncStatus(String emoji, String description, String telexStatus) {
        this.emoji = emoji;
        this.description = description;
        this.telexStatus = telexStatus;
    }

    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public String getTelexStatus() { return telexStatus; }
}
