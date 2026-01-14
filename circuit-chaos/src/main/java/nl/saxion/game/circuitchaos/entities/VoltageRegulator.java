package nl.saxion.game.circuitchaos.entities;

import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

public class VoltageRegulator extends WirePort{
    public VoltageRegulator(float x, float y, float size, PortColor color) {
        super(x, y, size);
        this.color = PortColor.WHITE;
    }

    @Override
    public int getMaxConnections() {
        return 4;
    }

    @Override
    public void draw() {
        float voltageRegulatorSize = Math.min(positionWidth, positionHeight); // example resize 130% - a bit bigger than the tile

        float centerX = positionX + (positionWidth - voltageRegulatorSize) / 2;
        float centerY = positionY + (positionHeight - voltageRegulatorSize) / 2;

        GameApp.drawTexture("voltage regulator", centerX, centerY, voltageRegulatorSize, voltageRegulatorSize);
    }
}
