package nl.saxion.game.circuitchaos.entities;

import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

public class VoltagePort extends WirePort{
    public VoltagePort(float x, float y, float size, PortColor color) {
        super(x, y, size);
        // All sides active by default
        for (int i = 0; i < 4; i++) {
            ports[i] = true;
        }
        this.color = PortColor.WHITE;
    }

    @Override
    public void draw() {
        float voltagePortSize = Math.min(positionWidth, positionHeight);

        float centerX = positionX + (positionWidth - voltagePortSize) / 2;
        float centerY = positionY + (positionHeight - voltagePortSize) / 2;

        GameApp.drawTexture("white port", centerX, centerY, voltagePortSize, voltagePortSize);
    }
}
