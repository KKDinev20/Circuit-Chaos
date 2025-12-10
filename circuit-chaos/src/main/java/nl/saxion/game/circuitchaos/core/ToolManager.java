package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.entities.Tool;
import nl.saxion.game.circuitchaos.util.GameConstants;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;

public class ToolManager {
    // Array of tools available in the toolbox
    private Tool[] toolboxTools;
    // List of tools that have been placed on the grid
    private List<Tool> placedTools;
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
        // Calculate Y position for tools (below the grid)
        float toolY = gridY - GameConstants.GRID_TOOL_SPACING - GameConstants.TOOL_SIZE;
        float startX = gridX + 15f;

        // Create each tool with its specific properties
        for (int i = 0; i < GameConstants.TOOL_COUNT; i++) {
            float toolX = startX + (i * GameConstants.TOOL_SPACING);
            toolboxTools[i] = new Tool(toolX, toolY, GameConstants.TOOL_SIZE, GameConstants.TOOL_SIZE,
                    GameConstants.TOOL_COLORS[i], 0);
            // Store original position for returning tools later
            originalToolX[i] = toolX;
            originalToolY[i] = toolY;
        }
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

    // Places a tool from the toolbox onto the grid at the mouse position
    public boolean placeTool(Tool tool, float gridX, float gridY, float mouseX, float mouseY) {
        // Calculate the grid cell position to snap to
        float[] cellPos = GridManager.getGridCellPosition(gridX, gridY, GameConstants.GRID_WIDTH, mouseX, mouseY);

        // Check if this cell is already occupied
        if (isCellOccupied(gridX, gridY, cellPos[0], cellPos[1])) {
            return false; // Placement failed - cell is occupied
        }

        // Create a new tool instance for the grid
        Tool placedTool = new Tool(
                cellPos[0], cellPos[1],
                GameConstants.TOOL_SIZE, GameConstants.TOOL_SIZE,
                tool.color, 0
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
        // Draw toolbox tools - these may be grayed out if all placements are used
        for (int i = 0; i < toolboxTools.length; i++) {
            Tool tool = toolboxTools[i];
            if (tool != null) {
                // Gray out tool if it can no longer be placed
                Color drawColor = tool.canBePlaced(GameConstants.MAX_PLACEMENTS[i], tool.usedPlacements)
                        ? tool.color : Color.GRAY;
                GameApp.drawRect(tool.x, tool.y, tool.width, tool.height, drawColor);
            }
        }

        // Draw tools that have been placed on the grid - these are always colorful
        for (Tool placedTool : placedTools) {
            GameApp.drawRect(placedTool.x, placedTool.y, placedTool.width, placedTool.height, placedTool.color);
        }
    }

    // Getter methods for accessing the tool arrays
    public Tool[] getToolboxTools() { return toolboxTools; }
    //public List<Tool> getPlacedTools() { return placedTools; }
}