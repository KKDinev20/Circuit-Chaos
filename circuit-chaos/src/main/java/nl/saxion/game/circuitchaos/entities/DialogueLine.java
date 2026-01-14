package nl.saxion.game.circuitchaos.entities;

public class DialogueLine {
    public String speaker;
    public String text;
    public boolean isLeftSpeaking;

    public DialogueLine(String speaker, String text, boolean isLeftSpeaking) {
        this.speaker = speaker;
        this.text = text;
        this.isLeftSpeaking = isLeftSpeaking;
    }
}