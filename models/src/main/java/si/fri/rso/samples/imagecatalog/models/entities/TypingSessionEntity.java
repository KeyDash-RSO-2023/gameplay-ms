package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "typing_session")
//@NamedQueries(value =
//        {
//                @NamedQuery(name = "ImageMetadataEntity.getAll",
//                        query = "SELECT im FROM ImageMetadataEntity im")
//        })
public class TypingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long typingSessionId;

    @Column(name = "textToType")
    private String textToType;

    @Column(name = "language")

    private String language;

    @Column(name = "length")
    private int length;

    @Column(name = "punctuation")
    private boolean punctuation;

    @Column(name = "startTime")
    private Instant startTime;

    @Column(name = "lastUpdateTime")
    private Instant lastUpdateTime;

    @Column(name = "endTime")
    private Instant endTime;

    @Column(name = "wpm")
    private Double wpm;

    @Column(name= "accuracy")
    private Double accuracy;

    @Column(name = "status")
    private String status;

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

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Instant getEndTime() {
        return endTime;
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