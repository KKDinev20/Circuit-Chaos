package nl.saxion.game.circuitchaos.entities;

public abstract class CircuitElement {
    public float positionX;
    public float positionY;

    public float positionWidth;
    public float positionHeight;

    public boolean[] ports = new boolean[4]; // N, E, S, W
    public boolean hasPower;

    public CircuitElement(float x, float y, float width, float height) {
        positionX = x;
        positionY = y;
        positionWidth = width;
        positionHeight = height;
    }

    // Simple collision check
    public boolean contains(float px, float py) {
        return px >= positionX && px <= positionX + positionWidth && py >= positionY && py <= positionY + positionHeight;
    }


    public abstract void draw();
    public abstract void update();

}
