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
}
