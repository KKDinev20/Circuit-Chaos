package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import nl.saxion.game.circuitchaos.core.GameState;
import nl.saxion.game.circuitchaos.ui.UIButton;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

import static nl.saxion.game.circuitchaos.util.HelperMethods.windowToWorldMouse;

public class GameWinScreen extends ScalableGameScreen {
    private boolean menuHover = false;

    public GameWinScreen() {
        super(1280, 720);
    }

    @Override
    public void show() {
        GameApp.addFont("winFont", "fonts/PressStart2P-Regular.ttf", 40);
        GameApp.addFont("subtitleFont", "fonts/PressStart2P-Regular.ttf", 25);
    }

    @Override
    public void render(float delta) {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());
        GameApp.clearScreen();

        float[] m = windowToWorldMouse(getWorldWidth(), getWorldHeight());
        float mx = m[0];
        float my = m[1];

        float buttonX = 50;
        float buttonY = 50; // some margin from top
        String buttonText = "Main menu";

        float textWidth = GameApp.getTextWidth("buttonFont", buttonText);
        float textHeight = GameApp.getTextHeight("buttonFont", buttonText);
        menuHover = mx >= getWorldWidth()/2f - textWidth/2 && mx <= getWorldWidth()/2f + textWidth/2 &&
                my >= buttonY - textHeight && my <= buttonY;

        // Handle click
        if (menuHover && GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Reset campaign so the player can start again
            GameState.highestCompletedLevel = 0;
            GameApp.switchScreen("MainMenuScreen");
        }

        // Draw background (game background)
        GameApp.startSpriteRendering();
        GameApp.drawTexture("level6", 0, 0, getWorldWidth(), getWorldHeight());
        GameApp.endSpriteRendering();

        String mainText = "Congratulations!";
        float x = getWorldWidth()/2f;
        float y = getWorldHeight()/2f + 275;
        float outlineOffset = 3f; // how thick the outline is

        GameApp.startSpriteRendering();

// Draw outline (8 directions)
        String[] directions = {"-x,-y","-x,0","-x,+y","0,-y","0,+y","+x,-y","+x,0","+x,+y"};
        float[] dx = {-outlineOffset, -outlineOffset, -outlineOffset, 0, 0, outlineOffset, outlineOffset, outlineOffset};
        float[] dy = {-outlineOffset, 0, outlineOffset, -outlineOffset, outlineOffset, -outlineOffset, 0, outlineOffset};

        for (int i = 0; i < dx.length; i++) {
            GameApp.drawTextCentered("winFont", mainText, x + dx[i], y + dy[i], "black");
        }
        GameApp.drawTextCentered("winFont", mainText, x, y, "yellow-400");

        GameApp.endSpriteRendering();

        // Draw Congratulations text
        GameApp.startSpriteRendering();
        GameApp.drawTextCentered("subtitleFont", "You saved the city!", getWorldWidth()/2f, getWorldHeight()/2f + 225, "white");

        // Draw Lumen below text
        GameApp.drawTexture("char_lumen", getWorldWidth()/2f - 125, getWorldHeight()/2f - 300, 250, 375);
        drawMenuText(buttonText, buttonY, menuHover);
        GameApp.endSpriteRendering();
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
        GameApp.disposeUIElements();
    }
}
