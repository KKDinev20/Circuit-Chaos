package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.enums.DialogueType;
import nl.saxion.game.circuitchaos.entities.enums.IntroType;
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

    public void startIntro(IntroType introType) {
        currentDialogue.clear();
        currentLineIndex = 0;
        dialogueActive = true;
        dialogueComplete = false;

        loadIntroDialogue(introType);
    }

    private void loadIntroDialogue(IntroType introType) {
        switch (introType) {
            case LUMEN_INTRO:
                currentBackgroundTexture = "level6";
                leftCharacterTexture = "char_lumen";
                rightCharacterTexture = null;

                currentDialogue.add(new DialogueLine("Lumen", "Hello! I am Lumen.", true));
                currentDialogue.add(new DialogueLine("Lumen", "Today is my first day at BrightSpark Repairs.", true));
                currentDialogue.add(new DialogueLine("Lumen", "Hopefully nothing goes wrong.", true));
                break;
            case NEXT_DAY:
                currentBackgroundTexture = "level6";
                leftCharacterTexture = null;
                rightCharacterTexture = null;

                currentDialogue.add(new DialogueLine("", "The next day...", true));
                break;

            case NEWS:
                currentBackgroundTexture = "news_background";
                leftCharacterTexture = null;
                rightCharacterTexture = "char_barbara";

                currentDialogue.add(new DialogueLine(
                        "Barbara (news)",
                        "Breaking news! Large parts of the city lost power overnight.",
                        false
                ));
                currentDialogue.add(new DialogueLine(
                        "Barbara (news)",
                        "Authorities are still investigating the cause.",
                        false
                ));
                currentDialogue.add(new DialogueLine(
                        "Barbara (news)",
                        "If your place was affected, please call the BrightSpark company, they will help you.",
                        false
                ));
                break;
        }
    }

    private void loadDialogueForLevel(int level, DialogueType type) {
        switch (level) {
            case 1:
                currentBackgroundTexture = "level1";
                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_sabrina";
                    currentBackgroundTexture = "level1";

                    currentDialogue.add(new DialogueLine(
                            "Sabrina",
                            "H-Hello? Is this BrightSpark Repairs? There is no power at my house!",
                            false
                    ));

                    currentDialogue.add(new DialogueLine(
                            "Sabrina",
                            "I...I need help...urgently! Please come as quick as possible!",
                            false
                    ));

                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Sabrina! Stay calm and do not do anything dangerous!",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I am coming to your house right now! I will be as fast as I can!",
                            true
                    ));
                }
                else if (type == DialogueType.POST_LEVEL_WIN) {
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I fixed your wires and connected the bulbs, everything should be working now.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Sabrina",
                            "Ah, thank you Lumen! You are a real life saver!",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "No problem, Sabrina! Uh oh, I am getting another call...",
                            true
                    ));
                } else {
                    currentDialogue.add(new DialogueLine("Sabrina", "The whole house is still powered off! I have assignments to submit!", false));
                    currentDialogue.add(new DialogueLine("Lumen", "I need to think more carefully about the connections!", true));
                }
                break;
            case 2:
                currentBackgroundTexture = "level2";

                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_konstantin";

                    currentDialogue.add(new DialogueLine("Konstantin", "Hello? Lumen? You must come quickly! The entire supermarket is dark!", false));
                    currentDialogue.add(new DialogueLine("Konstantin", "The freezers stopped, alarms are silent, customers are confused!", false));
                    currentDialogue.add(new DialogueLine("Konstantin", "If the cold storage fails, we lose everything!", false));
                    currentDialogue.add(new DialogueLine("Lumen", "Konstantin, stay calm. I am on my way.", true));
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
                            "People can not work, can not study — some can not even order coffee!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Alright, Georgi. Sounds like the circuits are broken.",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I will fix it using the switches. Just do not touch the wiring.",
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
                else {
                    currentDialogue.add(new DialogueLine(
                            "Andrea",
                            "Nothing’s working yet! People are confused and panicking.",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "I must have done something wrong. Let me check again.",
                            true
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
                }
                else if (type == DialogueType.POST_LEVEL_WIN) {

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
                else {
                    currentDialogue.add(new DialogueLine(
                            "Melany",
                            "It's not working, the devices will get fried if this goes on for long.",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Something must be wrong. Let me take a look once more.",
                            true
                    ));
                }
                break;
            case 6:
                currentBackgroundTexture = "level6";
                if (type == DialogueType.PRE_LEVEL) {
                    leftCharacterTexture = "char_lumen";
                    rightCharacterTexture = "char_technician";

                    currentDialogue.add(new DialogueLine(
                            "Technician",
                            "Lumen! You got to come back to the station! Everything just went off!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Technician",
                            "All the employees are working hard, but we can not cover everything. We need your help!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Oh no, that's bad. I will be there as fast as I can!",
                            true
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Try to get rid of as many problems as you can.",
                            true
                    ));
                }
                else if (type == DialogueType.POST_LEVEL_WIN) {
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Oh, just in time. The station is saved, now everyone should have power.",
                            true
                    ));

                    currentDialogue.add(new DialogueLine(
                            "Technician",
                            "Thank you for your service, Lumen! We couldn't have done this without you!",
                            false
                    ));
                }
                else {
                    currentDialogue.add(new DialogueLine(
                            "Technician",
                            "The station is still off! I don't think we will be able to handle it!",
                            false
                    ));
                    currentDialogue.add(new DialogueLine(
                            "Lumen",
                            "Just give me one more try, I think I found the problem.",
                            true
                    ));
                }
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
        // LEFT character
        if (leftCharacterTexture != null) {
            if (currentLine.isLeftSpeaking) {
                GameApp.setColor(255, 255, 255, 255);
            } else {
                GameApp.setColor(100, 100, 100, 255);
            }

            GameApp.drawTexture(
                    leftCharacterTexture,
                    leftCharX,
                    leftCharY,
                    250,
                    375,
                    0,
                    true,
                    false
            );
        }
        if (rightCharacterTexture != null) {
            if (!currentLine.isLeftSpeaking) {
                GameApp.setColor(255, 255, 255, 255);
            } else {
                GameApp.setColor(100, 100, 100, 255);
            }

            GameApp.drawTexture(
                    rightCharacterTexture,
                    rightCharX,
                    rightCharY,
                    250,
                    375,
                    0,
                    false,
                    false
            );
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

        GameApp.drawTexture("skip_button", skipButtonX, skipButtonY, skipButtonW, skipButtonH);

        GameApp.setColor(0, 0, 0, 255);
        String prompt = (currentLineIndex < currentDialogue.size() - 1) ? "[SPACE] Next" : "[SPACE] Start";
        GameApp.drawText("dialogueFont", prompt, dialogueBoxX + dialogueBoxW - 300, dialogueBoxY + 35, "black");

        GameApp.endSpriteRendering();

        if (skipHovered) {
            GameApp.startShapeRenderingOutlined();
            GameApp.setColor(0, 255, 0, 255);
            GameApp.drawRect(skipButtonX, skipButtonY, skipButtonW, skipButtonH);
            GameApp.endShapeRendering();
        }
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