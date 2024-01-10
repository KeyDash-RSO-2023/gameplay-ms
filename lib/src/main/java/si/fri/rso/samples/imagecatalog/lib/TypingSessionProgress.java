package si.fri.rso.samples.imagecatalog.lib;

public class TypingSessionProgress {

    private double currentWpm;
    private int typedWords;
    private double accuracy;

    // Default constructor
    public TypingSessionProgress() {
    }

    // Parameterized constructor
    public TypingSessionProgress(double currentWpm, int typedWords, double accuracy) {
        this.currentWpm = currentWpm;
        this.typedWords = typedWords;
        this.accuracy = accuracy;
    }

    // Getters and Setters

    public double getCurrentWpm() {
        return currentWpm;
    }

    public void setCurrentWpm(double currentWpm) {
        this.currentWpm = currentWpm;
    }

    public int getTypedWords() {
        return typedWords;
    }

    public void setTypedWords(int typedWords) {
        this.typedWords = typedWords;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
