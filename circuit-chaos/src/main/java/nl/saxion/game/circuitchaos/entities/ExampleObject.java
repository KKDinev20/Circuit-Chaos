package nl.saxion.game.circuitchaos.entities;

import com.badlogic.gdx.graphics.Color;


public class ExampleObject {
    public float x;
    public float y;
    public float width;
    public float height;
    public Color color;
    public int usedPlacements;

    public ExampleObject(float x, float y, float width, float height, Color color, int usedPlacements) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.usedPlacements = usedPlacements;
    }

    public boolean canBePlaced(int maxPlacements, int usedPlacements)
    {
       return usedPlacements < maxPlacements;
    }

    public boolean containsPoint(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }
}
