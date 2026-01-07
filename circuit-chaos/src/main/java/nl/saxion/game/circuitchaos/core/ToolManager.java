package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.entities.Tool;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.game.circuitchaos.util.GameConstants;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;

import static nl.saxion.game.circuitchaos.core.LevelManager.currentLevel;

public class ToolManager {
    // Array of tools available in the toolbox
    private Tool[] toolboxTools;
    // List of tools that have been placed on the grid
    private ArrayList<Tool> placedTools;
    // Store original positions for returning tools to toolbox
    private float[] originalToolX;
    private float[] originalToolY;


    public ToolManager() {
        toolboxTools = new Tool[GameConstants.TOOL_COUNT];
        placedTools = new ArrayList<>();
        originalToolX = new float[GameConstants.TOOL_COUNT];
        originalToolY = new float[GameConstants.TOOL_COUNT];

    }

    // Draw the tools in the toolbox below the grid
    public void initializeTools(float gridX, float gridY) {
        if (currentLevel != 3 && currentLevel != 6) {
            return; // Exit early; no tools for this level
        }

        // Calculate Y position for tools (below the grid)
        float toolY = gridY - GameConstants.GRID_TOOL_SPACING - GameConstants.TOOL_BOX_SIZE;
        float startX = gridX + (GameConstants.GRID_WIDTH - GameConstants.TOOL_BOX_SIZE) / 2f;

        toolboxTools[0] = new Tool(
                startX,
                toolY,
                GameConstants.TOOL_BOX_SIZE,
                GameConstants.TOOL_BOX_SIZE,
                PortColor.BLACK.getTextureName(),
                0
        );

        originalToolX[0] = startX;
        originalToolY[0] = toolY;
    }

    // Finds and returns a tool at the given screen coordinates
    // Returns null if no tool is found or if the tool cannot be placed
    public Tool getToolAtPosition(float x, float y) {
        for (int i = 0; i < toolboxTools.length; i++) {
            Tool tool = toolboxTools[i];
            // Check if tool exists, contains the point, and can still be placed
            if (tool != null && tool.containsPoint(x, y) &&
                    tool.canBePlaced(GameConstants.MAX_PLACEMENTS[i], tool.usedPlacements)) {
                return tool;
            }
        }
        return null;
    }

    public boolean isCellOccupied(float gridX, float gridY, float cellX, float cellY) {
        for (Tool placedTool : placedTools) {
            if (Math.abs(placedTool.x - cellX) < 1f && Math.abs(placedTool.y - cellY) < 1f) {
                return true;
            }
        }
        return false;
    }

    public boolean placeTool(Tool tool, float gridX, float gridY, float mouseX, float mouseY,
                             TileConnectionManager connectionManager) {
        // Calculate the grid cell position
        float[] cellPos = GridManager.getGridCellPosition(
                gridX, gridY, GameConstants.GRID_WIDTH, mouseX, mouseY
        );

        int gridCellX = (int) ((mouseX - gridX) / (GameConstants.GRID_WIDTH / GameConstants.GRID_SIZE));
        int gridCellY = (int) ((mouseY - gridY) / (GameConstants.GRID_WIDTH / GameConstants.GRID_SIZE));

        // CHECK: Must be placed on a wire path
        if (!connectionManager.hasWireAtCell(gridCellX, gridCellY)) {
            System.out.println("Cannot place port - no wire at this location!");
            return false;
        }

        // CHECK: Cell not already occupied by another tool
        if (isCellOccupied(gridX, gridY, cellPos[0], cellPos[1])) {
            System.out.println("Cannot place port - cell already occupied!");
            return false;
        }

        // Place the tool
        float placedSize = GameConstants.TOOL_SIZE;
        float cellSize = (float) GameConstants.GRID_WIDTH / GameConstants.GRID_SIZE;

        float centeredX = cellPos[0] + (cellSize / 2f) - (placedSize / 2f);
        float centeredY = cellPos[1] + (cellSize / 2f) - (placedSize / 2f);

        Tool placedTool = new Tool(
                centeredX,
                centeredY,
                placedSize,
                placedSize,
                tool.textureName,
                gridCellX, // Store grid position
                gridCellY
        );

        placedTools.add(placedTool);

        // Update the toolbox tool
        for (int i = 0; i < toolboxTools.length; i++) {
            if (toolboxTools[i] == tool) {
                toolboxTools[i].usedPlacements++;
                toolboxTools[i].x = originalToolX[i];
                toolboxTools[i].y = originalToolY[i];
                break;
            }
        }

        return true;
    }


    // Returns a tool to its original position in the toolbox
    public void returnToolToToolbox(Tool tool) {
        for (int i = 0; i < toolboxTools.length; i++) {
            if (toolboxTools[i] == tool) {
                toolboxTools[i].x = originalToolX[i];
                toolboxTools[i].y = originalToolY[i];
                break;
            }
        }
    }
    // Draws all tools - both in toolbox and placed on grid
    public void drawTools() {
        GameApp.addFont("basic", "fonts/basic.ttf", 24);

        for (Tool tool : toolboxTools) {
            if (tool != null) {
                // Icon
                float iconSize = tool.width * 0.85f;
                float iconX = tool.x + (tool.width - iconSize) / 2f;
                float iconY = tool.y + (tool.height - iconSize) / 2f + 6;

                GameApp.drawTexture(
                        tool.textureName,
                        iconX,
                        iconY,
                        iconSize,
                        iconSize
                );

                // Remaining uses
                int remainingUses =
                        GameConstants.MAX_PLACEMENTS[0] - tool.usedPlacements;

                GameApp.drawText(
                        "basic",
                        String.valueOf(remainingUses),
                        tool.x + tool.width / 2f,
                        tool.y + 14,
                        Color.WHITE
                );
            }
        }

        // Placed tools (no UI box)
        for (Tool placedTool : placedTools) {
            GameApp.drawTexture(
                    placedTool.textureName,
                    placedTool.x,
                    placedTool.y,
                    placedTool.width,
                    placedTool.height
            );
        }
    }

    // Getter methods for accessing the tool arrays
    public Tool[] getToolboxTools() {
        return toolboxTools;
    }
    public ArrayList<Tool> getPlacedTools() {
        return placedTools;
    }
}