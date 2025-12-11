package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.game.circuitchaos.levels.Level;
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

        GameApp.addTexture("background1", "textures/backgrounds/house.png");

        startX = getWorldWidth() / 2f - btnW / 2f;
        startY = getWorldHeight() / 2f - 200;

        backX = getWorldWidth() / 2f - btnW / 2f;
        backY = getWorldHeight() / 2f - 325;
    }

    public void render(float delta) {
        super.render(delta);

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        // Hover checks
        startHover = (mx >= startX && mx <= startX + btnW &&
                my >= startY && my <= startY + btnH);

        backHover = (mx >= backX && mx <= backX + btnW &&
                my >= backY && my <= backY + btnH);

        // Background
        GameApp.startSpriteRendering();
        GameApp.drawTexture("background1", 0, 0, getWorldWidth(), getWorldHeight());
        GameApp.endSpriteRendering();

        //-----------------------------
        // DRAW LEVEL TITLE IN CENTER
        //-----------------------------
        GameApp.startSpriteRendering();

        GameApp.drawTextCentered(
                "basic_large",
                "Level " + currentLevel,
                getWorldWidth() / 2f,
                getWorldHeight() / 2f + 150,
                "white"
        );

        GameApp.drawTextCentered(
                "basic_large",
                "<        >",
                getWorldWidth() / 2f,
                getWorldHeight() / 2f + 40,
                "gray-500"
        );

        GameApp.endSpriteRendering();

        // Start button
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(startX, startY, btnW, btnH, startHover ? "green-400" : "green-300");
        GameApp.endShapeRendering();

        if (startHover) {
            GameApp.startShapeRenderingOutlined();
            GameApp.drawRect(startX, startY, btnW, btnH, "white");
            GameApp.endShapeRendering();
        }

        GameApp.startSpriteRendering();
        GameApp.drawTextCentered("basic_large", "Start",
                startX + btnW / 2f, startY + btnH / 2f, "white");
        GameApp.endSpriteRendering();

        // Back button
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(backX, backY, btnW, btnH, backHover ? "red-400" : "red-300");
        GameApp.endShapeRendering();

        if (backHover) {
            GameApp.startShapeRenderingOutlined();
            GameApp.drawRect(backX, backY, btnW, btnH, "white");
            GameApp.endShapeRendering();
        }

        GameApp.startSpriteRendering();
        GameApp.drawTextCentered("basic_large", "Back",
                backX + btnW / 2f, backY + btnH / 2f, "white");
        GameApp.endSpriteRendering();

        handleInput();
        renderUI();
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
                GameApp.log("Start level " + currentLevel);
                GameApp.switchScreen("YourGameScreen");
            }

            if (backHover) {
                GameApp.switchScreen("MainMenuScreen");
            }
        }
    }

    private float[] windowToWorldMouse() {
        float wx = GameApp.getMousePositionInWindowX();
        float wy = GameApp.getMousePositionInWindowY();

        int winW = com.badlogic.gdx.Gdx.graphics.getWidth();
        int winH = com.badlogic.gdx.Gdx.graphics.getHeight();

        float worldW = getWorldWidth();
        float worldH = getWorldHeight();

        float sx = worldW / winW;
        float sy = worldH / winH;

        float worldX = wx * sx;
        float worldY = (winH - wy) * sy; // flip Y

        return new float[]{worldX, worldY};
    }

    public void hide() {
        GameApp.disposeUIElements();
    }
}
