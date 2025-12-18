package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuScreen extends ScalableGameScreen {

    private boolean startHovered = false, levelHovered = false, quitHovered = false;
    private float playY, levelSelectY, quitY;

    public MainMenuScreen() {
        super(1280, 720);
    }

    @Override
    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());
        // Load assets
        GameApp.addFont("buttonFont", "fonts/Cause-Medium.ttf", 40);
        GameApp.addTexture("background", "textures/backgrounds/main menu.png");

        playY = getWorldHeight() / 2f;
        levelSelectY = playY - 100;
        quitY = levelSelectY - 100;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Clear background
        GameApp.clearScreen("black");


        GameApp.startSpriteRendering();

        GameApp.drawTexture("background", 0, 0, getWorldWidth(), getWorldHeight());

        startHovered = isTextHovered("Play", playY);
        levelHovered = isTextHovered("Level Select", levelSelectY);
        quitHovered = isTextHovered("Quit", quitY);

        drawMenuText("Play", playY, startHovered);
        drawMenuText("Level Select", levelSelectY, levelHovered);
        drawMenuText("Quit", quitY, quitHovered);

        GameApp.endSpriteRendering();

        GameApp.startSpriteRendering();
        GameApp.endSpriteRendering();

        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (startHovered) {
                GameApp.switchScreen("YourGameScreen");
            }
            if (levelHovered) {
                GameApp.switchScreen("LevelSelectScreen");
            }
            if (quitHovered) {
                GameApp.quit();
            }
        }

        renderUI();
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

    private boolean isTextHovered(String text, float centerY) {
        float textWidth = GameApp.getTextWidth("buttonFont", text);
        float textHeight = GameApp.getTextHeight("buttonFont", text);

        float centerX = getWorldWidth() / 2f;

        float x = centerX - textWidth / 2f;
        float y = centerY - textHeight / 2f;

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        return (mx >= x && mx <= x + textWidth &&
                my >= y && my <= y + textHeight);
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

    /**
     * Called by the UI system when a UI element is interacted with.
     * key = the name passed to addButton / addCheckBox / etc.
     * value = the new value (not used for buttons).
     */

    @Override
    public void hide() {
        // Dispose UI elements + other assets
        GameApp.disposeUIElements();
        GameApp.disposeSkin("mainSkin");
    }
}
