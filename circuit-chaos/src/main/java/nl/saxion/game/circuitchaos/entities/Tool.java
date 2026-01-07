package nl.saxion.game.circuitchaos.entities;

public class Tool {
    public float x;
    public float y;
    public float width;
    public float height;
    public String textureName;
    public int usedPlacements;
    public int gridX = -1;
    public int gridY = -1;

    // Update constructor:
    public Tool(float x, float y, float width, float height, String textureName, int gridX, int gridY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureName = textureName;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public Tool(float x, float y, float width, float height, String textureName, int usedPlacements) {
        this(x, y, width, height, textureName, -1, -1);
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
