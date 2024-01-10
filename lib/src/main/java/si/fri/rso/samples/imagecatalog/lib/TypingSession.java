package si.fri.rso.samples.imagecatalog.lib;

import java.time.Instant;
import java.util.UUID;

public class TypingSession {
    private long typingSessionId;
    private String textToType;

    private String language;

    private int length;

    private boolean punctuation;

    private Instant startTime;

    private Instant lastUpdateTime;

    private Instant endTime;

    private Double wpm;

    private Double accuracy;

    private String status;

    public TypingSession() {
//        this.id = UUID.randomUUID().toString();
        this.textToType = "";
        this.startTime = Instant.now(); // Initialize with the current time
        this.endTime = null; // Initialize as null, to be set when the session ends
        this.wpm = 0.0; // Initialize with a default value, e.g., 0.0
        this.status = "in progress"; // Initialize with a default status, e.g., "new"
    }

    // Getters and Setters

    public long getTypingSessionId() {
        return typingSessionId;
    }

    public void setTypingSessionId(long typingSessionId) {
        this.typingSessionId = typingSessionId;
    }

    public String getTextToType() {
        return textToType;
    }

    public void setTextToType(String textToType) {
        this.textToType = textToType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPunctuation() {
        return punctuation;
    }

    public void setPunctuation(boolean punctuation) {
        this.punctuation = punctuation;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdatedime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Double getWpm() {
        return wpm;
    }

    public void setWpm(Double wpm) {
        this.wpm = wpm;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
