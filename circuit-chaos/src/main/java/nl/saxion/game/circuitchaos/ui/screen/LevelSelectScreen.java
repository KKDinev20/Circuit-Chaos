package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.game.circuitchaos.core.LevelManager;
import nl.saxion.game.circuitchaos.levels.Level;
import nl.saxion.game.circuitchaos.util.HelperMethods;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

public class LevelSelectScreen extends ScalableGameScreen {
    private int currentLevel = 1;
    private int levelCount = 6;

    private float btnW = 300;
    private float btnH = 100;

    private float startX, startY;
    private boolean startHover = false;

    private float backX, backY;
    private boolean backHover = false;

    public LevelSelectScreen() {
        super(1280, 720);
    }

    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());

        GameApp.addFont("levelSelectFont", "fonts/Cause-Medium.ttf", 50);
        GameApp.addFont("buttonFont", "fonts/Cause-Medium.ttf", 40);
        GameApp.addTexture("background1", "textures/backgrounds/house.png");
        GameApp.addTexture("background2", "textures/backgrounds/Supermarket.png");
        GameApp.addTexture("background3", "textures/backgrounds/coffee bar.png");
        GameApp.addTexture("background4", "textures/backgrounds/Mall.png");
        GameApp.addTexture("background5", "textures/backgrounds/electronics repair.png");
        GameApp.addTexture("background6", "textures/backgrounds/City power station.png");

        startX = getWorldWidth() / 2f - btnW / 2f;
        startY = getWorldHeight() / 2f - 150;

        backX = getWorldWidth() / 2f - btnW / 2f;
        backY = getWorldHeight() / 2f - 250;
    }

    public void render(float delta) {
        super.render(delta);

        float[] m = HelperMethods.windowToWorldMouse(getWorldWidth(), getWorldHeight());
        float mx = m[0];
        float my = m[1];

        // Hover checks
        startHover = (mx >= startX && mx <= startX + btnW &&
                my >= startY && my <= startY + btnH);

        backHover = (mx >= backX && mx <= backX + btnW &&
                my >= backY && my <= backY + btnH);

        // Background
        GameApp.startSpriteRendering();
        setBackground(currentLevel);
        GameApp.endSpriteRendering();

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

        startHover = isTextHovered("Start", startY);
        backHover = isTextHovered("Back", backY);

        GameApp.startSpriteRendering();
        drawMenuText("Start", startY, startHover);
        drawMenuText("Back", backY, backHover);
        GameApp.endSpriteRendering();

        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (startHover) {
                GameApp.switchScreen("YourGameScreen");
            }
            if (backHover) {
                GameApp.switchScreen("MainMenuScreen");
            }
        }

        handleInput();
        renderUI();
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

    private void setBackground(int currentLevel) {
        switch (currentLevel) {
            case 1:
                GameApp.drawTexture("background1", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 2:
                GameApp.drawTexture("background2", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 3:
                GameApp.drawTexture("background3", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 4:
                GameApp.drawTexture("background4", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 5:
                GameApp.drawTexture("background5", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 6:
                GameApp.drawTexture("background6", 0, 0, getWorldWidth(), getWorldHeight());
                break;
        }
    }

    private void handleInput() {
        // Left arrow
        if (GameApp.isKeyJustPressed(Input.Keys.LEFT)) {
            currentLevel--;
            if (currentLevel < 1) currentLevel = levelCount;
        }

        // Right arrow
        if (GameApp.isKeyJustPressed(Input.Keys.RIGHT)) {
            currentLevel++;
            if (currentLevel > levelCount) currentLevel = 1;
        }

        // Mouse click
        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {

            if (startHover) {
                LevelManager.currentLevel = currentLevel;

                // Switch screen
                GameApp.switchScreen("YourGameScreen");
            }

            if (backHover) {
                GameApp.switchScreen("MainMenuScreen");
            }
        }
    }

    public void hide() {
        GameApp.disposeUIElements();
    }
}
