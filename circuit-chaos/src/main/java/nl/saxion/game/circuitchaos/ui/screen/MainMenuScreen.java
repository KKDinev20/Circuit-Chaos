package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuScreen extends ScalableGameScreen {
    private float btnW, btnH;
    private float startX, startY;
    private float levelSelectX, levelSelectY;
    private float quitX, quitY;
    private boolean startHovered = false, levelHovered = false, quitHovered = false;

    public MainMenuScreen() {
        super(1280, 720);
    }

    @Override
    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());
        // Load assets
        GameApp.addFont("basic", "fonts/basic.ttf", 75);// normal font
        GameApp.addSkin("mainSkin", "skins/example-skin/skin.json");
        GameApp.addTexture("background", "textures/backgrounds/main menu.png");


        btnW = 300;
        btnH = 100;

        startX = getHUDWidth()/2f - btnW/2f;
        startY = getHUDHeight()/2f - btnH/2f;

        levelSelectX = startX;
        levelSelectY = startY - 125;

        quitX = startX;
        quitY = startY - 250;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        startHovered = (
                mx >= startX && mx <= startX + btnW &&
                        my >= startY && my <= startY + btnH
        );

        levelHovered = (mx >= levelSelectX && mx <= levelSelectX + btnW &&
                my >= levelSelectY && my <= levelSelectY + btnH);

        quitHovered = (mx >= quitX && mx <= quitX + btnW &&
                my >= quitY && my <= quitY + btnH);

        // Clear background
        GameApp.clearScreen("black");


        GameApp.startSpriteRendering();
        GameApp.drawTexture("background", 0, 0, getWorldWidth(), getWorldHeight());
        GameApp.endSpriteRendering();

        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(startX, startY, btnW, btnH, startHovered ? "blue-400" : "blue-300");
        GameApp.drawRect(levelSelectX, levelSelectY, btnW, btnH, levelHovered ? "blue-400" : "blue-300");
        GameApp.drawRect(quitX, quitY, btnW, btnH, quitHovered ? "blue-400" : "blue-300");
        GameApp.endShapeRendering();

        GameApp.startShapeRenderingOutlined();
        if (startHovered) {
            GameApp.drawRect(startX, startY, btnW, btnH, "white");
        }
        if (levelHovered) {
            GameApp.drawRect(levelSelectX, levelSelectY, btnW, btnH, "white");
        }
        if (quitHovered) {
            GameApp.drawRect(quitX, quitY, btnW, btnH, "white");
        }
        GameApp.endShapeRendering();

        GameApp.startSpriteRendering();
        GameApp.drawTextCentered(
                "basic",
                "Circuit Chaos",
                getWorldWidth() / 2f,
                getWorldHeight() - 150f,
                "amber-500"
        );

        GameApp.drawTextCentered("basic", "Play", startX + btnW / 2f,
                startY + btnH / 2f, "white");
        GameApp.drawTextCentered("basic", "Level Select", levelSelectX + btnW / 2f,
                levelSelectY + btnH / 2f, "white");
        GameApp.drawTextCentered("basic", "Quit", quitX + btnW / 2f,
                quitY + btnH / 2f, "white");
        GameApp.endSpriteRendering();

        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (startHovered) {
                GameApp.switchScreen("YourGameScreen");
            }
            if (levelHovered) {
                // to-do - add level selection screen
            }
            if (quitHovered) {
                GameApp.quit();
            }
        }

        handleInput();

        renderUI();
    }
    private void handleInput() {
        if (!GameApp.isButtonJustPressed(Input.Buttons.LEFT)) return;

        float mx = GameApp.getMousePositionInWindowX();
        float my = getWorldHeight() - GameApp.getMousePositionInWindowY();

        if (mx >= startX && mx <= startX + btnW &&
                my >= startY && my <= startY + btnH && GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {

            GameApp.log("Custom start clicked!");
            GameApp.switchScreen("YourGameScreen");
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

    /**
     * Called by the UI system when a UI element is interacted with.
     * key = the name passed to addButton / addCheckBox / etc.
     * value = the new value (not used for buttons).
     */
    @Override
    public void onUIInteraction(String key, Object value) {
        switch (key) {
            case "startButton":
                GameApp.log("Start button clicked!");
                GameApp.switchScreen("YourGameScreen"); // ensure this screen name exists & was registered
                break;
            case "quitButton":
                GameApp.log("Quit button clicked!");
                GameApp.quit();
                break;
            default:
                // pass other interactions through or handle them
                break;
        }
    }

    @Override
    public void hide() {
        // Dispose UI elements + other assets
        GameApp.disposeUIElements();   // recommended in docs
        GameApp.disposeFont("basic");
        GameApp.disposeFont("mainFont");
        GameApp.disposeSkin("mainSkin");
    }
}
