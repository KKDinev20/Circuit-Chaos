package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.enums.DialogueType;
import nl.saxion.gameapp.GameApp;
import java.util.ArrayList;
import nl.saxion.game.circuitchaos.entities.DialogueLine;

public class DialogueManager {
    private ArrayList<DialogueLine> currentDialogue;
    private int currentLineIndex;
    private boolean dialogueActive;
    private boolean dialogueComplete;
    private DialogueType currentType;
    private String currentBackgroundTexture;

    // Character positions
    private float leftCharX, leftCharY;
    private float rightCharX, rightCharY;
    private float dialogueBoxX, dialogueBoxY;
    private float dialogueBoxW, dialogueBoxH;

    // Character names for current dialogue
    private String leftCharacterTexture;
    private String rightCharacterTexture;

    // Skip button
    private float skipButtonX, skipButtonY, skipButtonW, skipButtonH;
    private boolean skipHovered = false;

    public DialogueManager() {
        currentDialogue = new ArrayList<>();
        currentLineIndex = 0;
        dialogueActive = false;
        dialogueComplete = false;
    }

    public void initialize(float worldWidth, float worldHeight) {
        dialogueBoxW = worldWidth * 0.95f;
        dialogueBoxH = worldHeight * 0.25f; // Reduced from 0.35f for better balance
        dialogueBoxX = (worldWidth - dialogueBoxW) / 2f;
        dialogueBoxY = worldHeight * 0f; // Set to bottom instead of worldHeight

        // Adjusted character positions to stand on/behind the box
        leftCharX = worldWidth * 0.10f;
        leftCharY = dialogueBoxY + 30; // Anchored relative to the box

        rightCharX = worldWidth * 0.65f;
        rightCharY = dialogueBoxY + 30;

        // Skip button (Top Right)
        skipButtonW = 220;
        skipButtonH = 80;
        skipButtonX = worldWidth - skipButtonW - 40;
        skipButtonY = worldHeight - skipButtonH - 40;

        GameApp.addFont("dialogueFont", "fonts/PressStart2P-Regular.ttf", 14);
        GameApp.addFont("dialogueNameFont", "fonts/PressStart2P-Regular.ttf", 18);
    }

    public void startDialogue(int level, DialogueType type) {
        currentDialogue.clear();
        currentLineIndex = 0;
        dialogueActive = true;
        dialogueComplete = false;
        currentType = type;
        currentBackgroundTexture = null;

        loadDialogueForLevel(level, type);
    }

    private void loadDialogueForLevel(int level, DialogueType type) {
        switch (level) {
            case 1:
                currentBackgroundTexture = "level1";
                break;
            case 2:
                currentBackgroundTexture = "level2";

                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_konstantin";

                    currentDialogue.add(new DialogueLine("Konstantin", "Hello? Lumen? You must come quickly! The entire supermarket is dark!", false));
                    currentDialogue.add(new DialogueLine("Konstantin", "The freezers stopped, alarms are silent, customers are confused!", false));
                    currentDialogue.add(new DialogueLine("Konstantin", "If the cold storage fails, we lose everything!", false));
                    currentDialogue.add(new DialogueLine("Lumen", "Konstantin, stay calm. I'm on my way.", true));
                    currentDialogue.add(new DialogueLine("Lumen", "Just keep people away from the cables.", true));
                } else if (type == DialogueType.POST_LEVEL_WIN) {
                    currentDialogue.add(new DialogueLine("Lumen", "Everything is connected again.", true));
                    currentDialogue.add(new DialogueLine("Lumen", "Freezers, cash registers, lights - running perfectly.", true));
                    currentDialogue.add(new DialogueLine("Konstantin", "You saved the store! Thank you, Lumen!", false));
                } else {
                    currentDialogue.add(new DialogueLine("Konstantin", "The freezers are still off! We're running out of time!", false));
                    currentDialogue.add(new DialogueLine("Lumen", "I need to think more carefully about the connections!", true));
                }
                break;
            case 3:
                currentBackgroundTexture = "level3";

                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_georgi";

                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "Lumen! My coffee bar is a disaster right now!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "The lights went out, the espresso machine shut down, even the Wi-Fi is gone!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "People can’t work, can’t study—some can’t even order coffee!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Alright, Georgi. Sounds like the circuits are broken.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I’ll fix it using the switches. Just don’t touch the wiring.",
                            true
                    ));
                } else if (type == DialogueType.POST_LEVEL_WIN) {

                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "All switches are set correctly. Power is flowing again.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Lights, espresso machine, and Wi-Fi are fully operational.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "You did it! The bar is alive again!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "You’re the best, Lumen! Free cappuccinos forever!",
                            false
                    ));
                } else {
                    currentDialogue.add(new DialogueLine(
                            "Georgi",
                            "Nothing’s working yet! The customers are getting restless!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Some switches must still be wrong. I need to redirect the current.",
                            true
                    ));
                }
                break;
            case 4:
                currentBackgroundTexture = "level4";
                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_andrea";

                    currentDialogue.add(new DialogueLine(
                            "Andrea",
                            "Hi Lumen, sorry to bother you, but the mall’s entire east wing just shut down!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Andrea",
                            "The elevators stopped, the shops can’t open their registers, and people are stuck!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I’m on my way. Keep everyone calm and away from the panels.",
                            true
                    ));
                } else if (type == DialogueType.POST_LEVEL_WIN) {
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "The extender is working now and the light bulb are replaced and stabilized. The mall is back online.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Andrea",
                            "You saved us! Thank you so much, Lumen!",
                            false
                    ));
                    }

                break;
            case 5:
                currentBackgroundTexture = "level5";
                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_melany";

                    currentDialogue.add(new DialogueLine(
                            "Melany",
                            "Lumen! My shop is full of sensitive equipment, and the blackout caused voltage spikes everywhere!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Melany",
                            "Nothing turns on without shorting out. I need your help!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I’m on my way. Don’t run any devices until I fix the circuits.",
                            true
                    ));
                }else if (type == DialogueType.POST_LEVEL_WIN) {

                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Voltage is stable. Your equipment is safe again.",
                            true
                    ));

                    currentDialogue.add(new DialogueLine(
                            "Melany",
                            "You saved me a fortune! Thank you, Lumen!",
                            false
                    ));
                }


                break;
            case 6:
                currentBackgroundTexture = "level6";
                break;
            default:
                currentDialogue.add(new DialogueLine("Lumen", "Time to get to work!", true));
        }
    }

    public void advance() {
        if (currentLineIndex < currentDialogue.size() - 1) {
            currentLineIndex++;
        } else {
            dialogueComplete = true;
        }
    }

    public void skipDialogue() {
        dialogueComplete = true;
        this.dialogueActive = false;
    }

    public void updateSkipButton(float mouseX, float mouseY) {
        skipHovered = mouseX >= skipButtonX && mouseX <= skipButtonX + skipButtonW &&
                mouseY >= skipButtonY && mouseY <= skipButtonY + skipButtonH;
    }

    public boolean isSkipButtonClicked(float mouseX, float mouseY) {
        return skipHovered;
    }

    public void draw(float worldWidth, float worldHeight) {
        if (!dialogueActive) return;

        GameApp.startSpriteRendering();

        // 1. Draw Background
        if (currentBackgroundTexture != null) {
            GameApp.setColor(255, 255, 255, 255);
            GameApp.drawTexture(currentBackgroundTexture, 0, 0, worldWidth, worldHeight);
        }

        DialogueLine currentLine = getCurrentLine();
        if (currentLine == null) {
            GameApp.endSpriteRendering();
            return;
        }

        // 2. Draw Characters (Dim the one not speaking)
        if (currentLine.isLeftSpeaking) {
            GameApp.setColor(255, 255, 255, 255);
            GameApp.drawTexture(leftCharacterTexture, leftCharX, leftCharY, 250, 300, 0, true, false);

            GameApp.setColor(100, 100, 100, 255);
            GameApp.drawTexture(rightCharacterTexture, rightCharX, rightCharY, 250, 300, 0, false, false);
        } else {
            GameApp.setColor(100, 100, 100, 255);
            GameApp.drawTexture(leftCharacterTexture, leftCharX, leftCharY, 250, 300, 0, true, false);

            GameApp.setColor(255, 255, 255, 255);
            GameApp.drawTexture(rightCharacterTexture, rightCharX, rightCharY, 250, 300, 0, false, false);
        }

        // 3. Draw Dialogue Box Texture
        GameApp.setColor(255, 255, 255, 255);
        GameApp.drawTexture("dialogue_box", dialogueBoxX, dialogueBoxY, dialogueBoxW, dialogueBoxH);

        GameApp.drawText(
                "dialogueNameFont",
                currentLine.speaker,
                dialogueBoxX + 60,
                dialogueBoxY + dialogueBoxH - 70,
                "black"
        );

        drawWrappedText(
                currentLine.text,
                dialogueBoxX + 60,
                dialogueBoxY + dialogueBoxH - 100,
                (int) (dialogueBoxW - 120),
                28
        );

        if (skipHovered) {
            GameApp.setColor(200, 255, 200, 255); // Slight tint on hover
        } else {
            GameApp.setColor(255, 255, 255, 255);
        }
        GameApp.drawTexture("skip_button", skipButtonX, skipButtonY, skipButtonW, skipButtonH);

        GameApp.setColor(0, 0, 0, 255);
        String prompt = (currentLineIndex < currentDialogue.size() - 1) ? "[SPACE] Next" : "[SPACE] Start";
        GameApp.drawText("dialogueFont", prompt, dialogueBoxX + dialogueBoxW - 300, dialogueBoxY + 35, "black");

        GameApp.endSpriteRendering();
    }

    private void drawWrappedText(String text, float x, float startY, int maxWidth, int lineHeight) {
        String[] words = text.split(" ");
        ArrayList<String> lines = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float textWidth = GameApp.getTextWidth("dialogueFont", testLine);

            if (textWidth > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        float y = startY;

        for (String line : lines) {
            if (y < dialogueBoxY + 40) break;

            GameApp.drawText("dialogueFont", line, x, y, "black");
            y -= lineHeight;
        }
    }


    public DialogueLine getCurrentLine() {
        if (currentLineIndex >= 0 && currentLineIndex < currentDialogue.size()) {
            return currentDialogue.get(currentLineIndex);
        }
        return null;
    }

    public boolean isActive() {
        return dialogueActive;
    }

    public boolean isComplete() {
        return dialogueComplete;
    }

    public void close() {
        dialogueActive = false;
    }
}