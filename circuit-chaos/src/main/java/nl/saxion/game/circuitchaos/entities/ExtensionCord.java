package nl.saxion.game.circuitchaos.entities;

import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

public class ExtensionCord extends WirePort {

    public ExtensionCord(float x, float y, float size) {
        super(x, y, size);

        // Only top and bottom ports are valid
        ports[0] = true;  // NORTH
        ports[1] = false; // EAST
        ports[2] = true;  // SOUTH
        ports[3] = false; // WEST
    }

    @Override
    public int getMaxConnections() {
        return 2;
    }

    @Override
    public void draw() {
        float extensionCordSize = Math.min(positionWidth, positionHeight); // example resize 130% - a bit bigger than the tile

        float centerX = positionX + (positionWidth - extensionCordSize) / 2;
        float centerY = positionY + (positionHeight - extensionCordSize) / 2 - 5;

        GameApp.drawTexture("extension cord", centerX, centerY, extensionCordSize, extensionCordSize);
    }
}
