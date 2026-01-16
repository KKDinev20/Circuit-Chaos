package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.game.circuitchaos.core.*;
import nl.saxion.game.circuitchaos.util.HelperMethods;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

public class LevelSelectScreen extends ScalableGameScreen {
    private int currentLevel = 1;

    private float btnW = 300;

    private float startY;

    private float backY;

    // Keyboard navigation
    private int selectedOption = 0; // 0 = Start, 1 = Back
    private final int OPTION_COUNT = 2;

    public LevelSelectScreen() {
        super(1280, 720);
    }

    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());

        GameApp.addFont("levelSelectFont", "fonts/Cause-Medium.ttf", 50);
        GameApp.addFont("buttonFont", "fonts/Cause-Medium.ttf", 40);
        ElementManager.addTextures();

        float startX = getWorldWidth() / 2f - btnW / 2f;
        startY = getWorldHeight() / 2f - 150;

        float backX = getWorldWidth() / 2f - btnW / 2f;
        backY = getWorldHeight() / 2f - 250;

        selectedOption = 0; // Reset to first option
    }

    public void render(float delta) {
        super.render(delta);

        float[] m = HelperMethods.windowToWorldMouse(getWorldWidth(), getWorldHeight());
        float mx = m[0];
        float my = m[1];

        // Handle keyboard navigation FIRST
        handleKeyboardNavigation();

        // Background
        HelperMethods.setBackground(currentLevel);

        //-----------------------------
        // DRAW LEVEL TITLE IN CENTER
        //-----------------------------
        GameApp.startSpriteRendering();

        GameApp.drawTextCentered(
                "levelSelectFont",
                "Level " + currentLevel,
                getWorldWidth() / 2f,
                getWorldHeight() - 100,
                "white"
        );

        GameApp.drawTextCentered(
                "levelSelectFont",
                "<        >",
                getWorldWidth() / 2f,
                getWorldHeight() / 2f + 40,
                "gray-500"
        );

        GameApp.endSpriteRendering();

        // Check mouse hover
        boolean startHover = isTextHovered("Start", startY);
        boolean backHover = isTextHovered("Back", backY);

        // Override with keyboard selection if no mouse hover
        if (!startHover && !backHover) {
            if (selectedOption == 0) startHover = true;
            else if (selectedOption == 1) backHover = true;
        } else {
            // Update selectedOption based on mouse hover
            if (startHover) selectedOption = 0;
            else selectedOption = 1;
        }

        GameApp.startSpriteRendering();
        drawMenuText("Start", startY, startHover);
        drawMenuText("Back", backY, backHover);
        GameApp.endSpriteRendering();

        // Handle click/enter
        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT) || GameApp.isKeyJustPressed(Input.Keys.ENTER)) {
            if (startHover) {
                LevelManager.currentLevel = currentLevel;
                GameApp.switchScreen("YourGameScreen");
            }
            if (backHover) {
                GameApp.switchScreen("MainMenuScreen");
            }
        }

        renderUI();
    }

    private void handleKeyboardNavigation() {
        // Up/Down for menu options
        if (GameApp.isKeyJustPressed(Input.Keys.UP) || GameApp.isKeyJustPressed(Input.Keys.W)) {
            selectedOption--;
            if (selectedOption < 0) selectedOption = OPTION_COUNT - 1;
        }

        if (GameApp.isKeyJustPressed(Input.Keys.DOWN) || GameApp.isKeyJustPressed(Input.Keys.S)) {
            selectedOption++;
            if (selectedOption >= OPTION_COUNT) selectedOption = 0;
        }

        // Left/Right for level selection
        int levelCount = 6;
        if (GameApp.isKeyJustPressed(Input.Keys.LEFT) || GameApp.isKeyJustPressed(Input.Keys.A)) {
            currentLevel--;
            if (currentLevel < 1) currentLevel = levelCount;
        }

        if (GameApp.isKeyJustPressed(Input.Keys.RIGHT) || GameApp.isKeyJustPressed(Input.Keys.D)) {
            currentLevel++;
            if (currentLevel > levelCount) currentLevel = 1;
        }
    }

    private void drawMenuText(String text, float centerY, boolean isHovered) {
        float centerX = getWorldWidth() / 2f;

        if (isHovered) {
            GameApp.drawText("buttonFont", "<", centerX + GameApp.getTextWidth("buttonFont", text) / 2 + 25, centerY - 20, "white");
        }

        GameApp.drawTextCentered("buttonFont", text, centerX, centerY, "white");

        if (isHovered) {
            float width = GameApp.getTextWidth("buttonFont", text);
            float height = GameApp.getTextHeight("buttonFont", text);

            GameApp.endSpriteRendering();

            GameApp.startShapeRenderingFilled();
            GameApp.drawRect(centerX - width / 2, centerY - height / 2 - 20, width, 10, "white");
            GameApp.endShapeRendering();

            GameApp.startSpriteRendering();
        }
    }

    private boolean isTextHovered(String text, float centerY) {
        float textWidth = GameApp.getTextWidth("buttonFont", text);
        float textHeight = GameApp.getTextHeight("buttonFont", text);

        float centerX = getWorldWidth() / 2f;

        float x = centerX - textWidth / 2f;
        float y = centerY - textHeight / 2f;

        float[] m = HelperMethods.windowToWorldMouse(getWorldWidth(), getWorldHeight());
        float mx = m[0];
        float my = m[1];

        return (mx >= x && mx <= x + textWidth &&
                my >= y && my <= y + textHeight);
    }

    public void hide() {
        GameApp.disposeUIElements();
    }
}