// Bulb.java - Simple with textures
package nl.saxion.game.circuitchaos.entities;

import nl.saxion.gameapp.GameApp;

public class Bulb extends CircuitElement {
    private boolean isLit = false;

    public Bulb(float x, float y, float size) {
        super(x, y, size, size);

        for (int i = 0; i < 4; i++) {
            ports[i] = true;
        }
    }

    public Bulb(float x, float y, float size,
                boolean north, boolean east, boolean south, boolean west) {
        super(x, y, size, size);
        ports[0] = north;
        ports[1] = east;
        ports[2] = south;
        ports[3] = west;
    }

    @Override
    public void update() {
        isLit = hasPower;
    }

    // In Bulb.java - draw method
    @Override
    public void draw() {
        String texture = isLit ? "bulb_on" : "bulb_off";

        // Calculate centered position
        float textureWidth = 35f;  // Your texture width
        float textureHeight = 50f; // Your texture height

        // Center the texture within the cell
        float centerX = positionX + (positionWidth - textureWidth) / 2;
        float centerY = positionY + (positionHeight - textureHeight) / 2;

        GameApp.drawTexture(texture, centerX, centerY, textureWidth, textureHeight);
    }

}