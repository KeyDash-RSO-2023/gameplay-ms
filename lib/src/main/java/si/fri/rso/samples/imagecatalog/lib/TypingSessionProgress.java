package si.fri.rso.samples.imagecatalog.lib;

public class TypingSessionProgress {

    private double currentWpm;
    private String typedText;
    private double accuracy;

    // Default constructor
    public TypingSessionProgress() {
    }

    // Parameterized constructor
    public TypingSessionProgress(double currentWpm, String typedText, double accuracy) {
        this.currentWpm = currentWpm;
        this.typedText = typedText;
        this.accuracy = accuracy;
    }

    // Getters and Setters

    public double getCurrentWpm() {
        return currentWpm;
    }

    public void setCurrentWpm(double currentWpm) {
        this.currentWpm = currentWpm;
    }

    public String getTypedText() {
        return typedText;
    }

    public void setTypedText(String typedText) {
        this.typedText = typedText;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
