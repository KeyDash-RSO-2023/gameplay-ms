package si.fri.rso.samples.imagecatalog.models.models;

public class TypingSessionInput {

    private String typedText;

    private int userId;

    public String getTypedText() {
        return typedText;
    }

    public void setTypedText(String typedText) {
        this.typedText = typedText;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
