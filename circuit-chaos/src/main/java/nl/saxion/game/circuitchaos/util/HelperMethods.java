package nl.saxion.game.circuitchaos.util;

import nl.saxion.gameapp.GameApp;

import static nl.saxion.gameapp.GameApp.getWorldHeight;
import static nl.saxion.gameapp.GameApp.getWorldWidth;

public class HelperMethods {
    public static float[] windowToWorldMouse(float worldW, float worldH) {
        float wx = GameApp.getMousePositionInWindowX();
        float wy = GameApp.getMousePositionInWindowY();

        int winW = com.badlogic.gdx.Gdx.graphics.getWidth();
        int winH = com.badlogic.gdx.Gdx.graphics.getHeight();

        float sx = worldW / winW;
        float sy = worldH / winH;

        float worldX = wx * sx;
        float worldY = (winH - wy) * sy; // flip Y

        return new float[]{worldX, worldY};
    }

    public static void setBackground(int currentLevel) {
        GameApp.startSpriteRendering();
        switch (currentLevel) {
            case 1:
                GameApp.drawTexture("level1", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 2:
                GameApp.drawTexture("level2", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 3:
                GameApp.drawTexture("level3", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 4:
                GameApp.drawTexture("level4", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 5:
                GameApp.drawTexture("level5", 0, 0, getWorldWidth(), getWorldHeight());
                break;
            case 6:
                GameApp.drawTexture("level6", 0, 0, getWorldWidth(), getWorldHeight());
                break;
        }
        GameApp.endSpriteRendering();
    }
}
