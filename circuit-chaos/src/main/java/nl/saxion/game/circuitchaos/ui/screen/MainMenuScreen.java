package nl.saxion.game.circuitchaos.ui.screen;

import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuScreen extends ScalableGameScreen {

    public MainMenuScreen() {
        super(1280, 720);
    }

    @Override
    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());
        // Load assets
        GameApp.addFont("basic", "fonts/basic.ttf", 150);// normal font
        GameApp.addSkin("mainSkin", "skins/example-skin/skin.json");


        // Add UI components (use the named API from the docs)
        GameApp.addButton(
                "startButton",      // name (key)
                "mainSkin",         // skin key
                getWorldWidth() / 2f - 200f, // x (bottom-left), center the button by subtracting half width
                getWorldHeight() / 2f - 75f, // y
                400f,               // width
                100f,                // height
                "Start"             // text
        );
        GameApp.addButton(
                "levelSelect",
                "mainSkin",
                getWorldWidth() / 2f - 200f,
                getWorldHeight() / 2f - 175f,
                400f,
                100f,
                "Level Select"
        );
        GameApp.addButton(
                "quitButton",
                "mainSkin",
                getWorldWidth() / 2f - 200f,
                getWorldHeight() / 2f - 275f,
                400f,
                100f,
                "Quit"
        );
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Clear background and draw world/title
        GameApp.clearScreen("black");

        GameApp.startSpriteRendering();
        GameApp.drawTextCentered(
                "basic",
                "Circuit Chaos",
                getWorldWidth() / 2f,
                getWorldHeight() - 150f,
                "amber-500"
        );
        GameApp.endSpriteRendering();

        // Render the UI elements (required by docs)
        renderUI();
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
