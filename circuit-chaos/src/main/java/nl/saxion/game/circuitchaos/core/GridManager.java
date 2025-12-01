package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.gameapp.GameApp;
import nl.saxion.game.circuitchaos.util.GameConstants;

public class GridManager {

    // Draws a grid with the specified position and dimensions
    public static void drawGrid(float x, float y, float width, float height) {
        GameApp.drawRect(x, y, width, height, Color.WHITE);

        // Draw grid lines to create cells
        float cellSize = width / GameConstants.GRID_SIZE;
        for (int i = 1; i < GameConstants.GRID_SIZE; i++) {
            float offset = cellSize * i;

            GameApp.drawLine(x + offset, y, x + offset, y + height, Color.BLUE);
            GameApp.drawLine(x, y + offset, x + width, y + offset, Color.BLUE);
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