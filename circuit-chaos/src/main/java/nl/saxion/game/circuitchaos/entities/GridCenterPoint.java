package nl.saxion.game.circuitchaos.entities;

public class GridCenterPoint {
    public float gridX, gridY; // Grid coordinates (0-5)
    public float centerX, centerY; // Screen coordinates

    public GridCenterPoint(float gridX, float gridY, float centerX, float centerY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.centerX = centerX;
        this.centerY = centerY;
    }
}