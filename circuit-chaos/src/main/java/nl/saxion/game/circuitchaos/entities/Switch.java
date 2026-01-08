package nl.saxion.game.circuitchaos.entities;

import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

public class Switch extends CircuitElement {
    private boolean isOn = false;

    public Switch(float x, float y, float size, PortColor color) {
        super(x, y, size, size);
        this.color = color;

        // Switches have ports on all 4 sides
        for (int i = 0; i < 4; i++) {
            ports[i] = true;
        }
    }

    @Override
    public void update() {
        // Switch doesn't generate power, it controls flow
        // Power flows through if switch is ON
    }

    @Override
    public void draw() {
        String texture = isOn ? "switch_on" : "switch_off";

        float textureWidth = 40f;
        float textureHeight = 60f;

        float centerX = positionX + (positionWidth - textureWidth) / 2;
        float centerY = positionY + (positionHeight - textureHeight) / 2;

        GameApp.drawTexture(texture, centerX, centerY, textureWidth, textureHeight);
    }

    // Toggle switch on/off when clicked
    public void toggle() {
        isOn = !isOn;
        System.out.println("Switch toggled to: " + (isOn ? "ON" : "OFF"));
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        this.isOn = on;
    }

    @Override
    public int getMaxConnections() {
        return 3;
    }
}