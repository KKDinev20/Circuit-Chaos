package nl.saxion.game.circuitchaos.entities;

import nl.saxion.gameapp.GameApp;

public class PowerPlug extends WirePort {
    public PowerPlug(float x, float y, float size) {
        super(x, y, size); // call the WirePort constructor
        // All sides active by default
        for (int i = 0; i < 4; i++) {
            ports[i] = true;
        }
    }

    @Override
    public int getMaxConnections() {
        return 1; // Only one wire can connect to a plug
    }

    @Override
    public void draw() {
        float plugSize = Math.min(positionWidth, positionHeight) * 0.9f;

        float centerX = positionX + (positionWidth - plugSize) / 2;
        float centerY = positionY + (positionHeight - plugSize) / 2;

        GameApp.drawTexture("plug", centerX, centerY, plugSize, plugSize);
    }
}
