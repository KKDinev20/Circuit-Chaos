package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

public class MainMenuScreen extends ScalableGameScreen {

    private boolean startHovered = false, levelHovered = false, quitHovered = false;
    private float playY, levelSelectY, quitY;

    // Keyboard navigation
    private int selectedOption = 0; // 0 = Play, 1 = Level Select, 2 = Quit

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

        selectedOption = 0; // Reset to first option
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Clear background
        GameApp.clearScreen("black");

        // Handle keyboard navigation
        handleKeyboardNavigation();

        GameApp.startSpriteRendering();

        GameApp.drawTexture("background", 0, 0, getWorldWidth(), getWorldHeight());

        // Check mouse hover
        startHovered = isTextHovered("Play", playY);
        levelHovered = isTextHovered("Level Select", levelSelectY);
        quitHovered = isTextHovered("Quit", quitY);

        // Override with keyboard selection if no mouse hover
        if (!startHovered && !levelHovered && !quitHovered) {
            if (selectedOption == 0) startHovered = true;
            else if (selectedOption == 1) levelHovered = true;
            else if (selectedOption == 2) quitHovered = true;
        } else {
            // Update selectedOption based on mouse hover
            if (startHovered) selectedOption = 0;
            else if (levelHovered) selectedOption = 1;
            else if (quitHovered) selectedOption = 2;
        }

        drawMenuText("Play", playY, startHovered);
        drawMenuText("Level Select", levelSelectY, levelHovered);
        drawMenuText("Quit", quitY, quitHovered);

        GameApp.endSpriteRendering();

        // Handle click/enter
        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT) || GameApp.isKeyJustPressed(Input.Keys.ENTER)) {
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

    private void handleKeyboardNavigation() {
        int OPTION_COUNT = 3;
        if (GameApp.isKeyJustPressed(Input.Keys.UP) || GameApp.isKeyJustPressed(Input.Keys.W)) {
            selectedOption--;
            if (selectedOption < 0) selectedOption = OPTION_COUNT - 1;
        }

        if (GameApp.isKeyJustPressed(Input.Keys.DOWN) || GameApp.isKeyJustPressed(Input.Keys.S)) {
            selectedOption++;
            if (selectedOption >= OPTION_COUNT) selectedOption = 0;
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

    @Override
    public void hide() {
        // Dispose UI elements + other assets
        GameApp.disposeUIElements();
        GameApp.disposeSkin("mainSkin");
    }
}