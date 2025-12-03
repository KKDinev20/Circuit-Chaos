package nl.saxion.game.circuitchaos.util;

import com.badlogic.gdx.graphics.Color;

// All reusable values in the game into one file.
public class GameConstants {
    // Grid dimensions
    public static final int GRID_SIZE = 6;
    public static final int GRID_WIDTH = 400;
    public static final int GRID_HEIGHT = 400;

    // Tools
    public static final int TOOL_COUNT = 4;
    public static final int[] MAX_PLACEMENTS = {3, 2, 1, 1}; // placeholder values
    public static final Color[] TOOL_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW}; // placeholder values

    // UI
    public static final int HEARTS_COUNT = 3;
    public static final float TOOL_SIZE = 66.67f; // size of one tile
    public static final float TOOL_SPACING = 100f;
    public static final float HEARTS_SIZE = 35f;
    public static final float HEARTS_SPACING = 10f;

    // Positioning & Spacing of objects
    public static final float GRID_TOOL_SPACING = 40f;
    public static final float GRID_HEARTS_SPACING = 35f;
}