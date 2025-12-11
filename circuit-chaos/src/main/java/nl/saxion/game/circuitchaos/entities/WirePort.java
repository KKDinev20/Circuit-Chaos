// Generator.java - Simple with texture
package nl.saxion.game.circuitchaos.entities;

import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

public class WirePort extends CircuitElement {
    private PortColor color;
    public WirePort(float x, float y, float size, PortColor color) {
        super(x, y, size, size);
        hasPower = true;
        for (int i = 0; i < 4; i++) {
            ports[i] = true;
        }
        this.color = color;
    }

    public WirePort(float x, float y, float size,
                     boolean north, boolean east, boolean south, boolean west) {
        super(x, y, size, size);
        hasPower = true;
        ports[0] = north;
        ports[1] = east;
        ports[2] = south;
        ports[3] = west;
    }

    @Override
    public void update() {
        // Generator stays powered (does nothing)
    }

    @Override
    public void draw() {
        String textureName = color.getTextureName();
        float portSize = Math.min(positionWidth, positionHeight) * 0.9f; // example resize 130% - a bit bigger than the tile


        float centerX = positionX + (positionWidth - portSize) / 2;
        float centerY = positionY + (positionHeight - portSize) / 2;

        GameApp.drawTexture(textureName, centerX, centerY, portSize, portSize);
    }

}