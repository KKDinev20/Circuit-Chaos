package nl.saxion.game.circuitchaos.entities;

public class GridCenterPoint {
    public float gridX, gridY;
    public float centerX, centerY;
    public float tileSize;

    public boolean isHorizontal;

    public GridCenterPoint(float gridX, float gridY,
                           float centerX, float centerY,
                           float tileSize,
                           boolean isHorizontal) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.centerX = centerX;
        this.centerY = centerY;
        this.tileSize = tileSize;
        this.isHorizontal = isHorizontal;
    }
}
