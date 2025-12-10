package nl.saxion.game.circuitchaos.core;

import nl.saxion.gameapp.GameApp;
import nl.saxion.game.circuitchaos.util.GameConstants;

public class GridManager {

    // Draws a grid with the specified position and dimensions
    public static void drawGrid(float x, float y, float width) {
        float cellSize = width / GameConstants.GRID_SIZE;

        GameApp.addTexture("Grid Tile","textures/tile.png");
        for (int gx = 0; gx < GameConstants.GRID_SIZE; gx++) {
            for (int gy = 0; gy < GameConstants.GRID_SIZE; gy++) {
                float cellX = x + gx * cellSize;
                float cellY = y + gy * cellSize;

                GameApp.drawTexture("Grid Tile", cellX, cellY, cellSize, cellSize);
            }
        }
    }


    // Calculates the grid cell position based on mouse coordinates
    // Returns the x and y position where a tool should be placed on the grid
    public static float[] getGridCellPosition(float gridX, float gridY, float gridWidth, float mouseX, float mouseY) {
        float cellSize = gridWidth / GameConstants.GRID_SIZE;
        // Calculate which grid cell the mouse is over
        int gridCellX = (int)((mouseX - gridX) / cellSize);
        int gridCellY = (int)((mouseY - gridY) / cellSize);

        // Return the actual screen coordinates for that grid cell
        return new float[] {
                gridX + (gridCellX * cellSize),
                gridY + (gridCellY * cellSize)
        };
    }
}