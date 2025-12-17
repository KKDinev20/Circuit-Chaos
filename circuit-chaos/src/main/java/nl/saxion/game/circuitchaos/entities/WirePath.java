package nl.saxion.game.circuitchaos.entities;

import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;

public class WirePath {

    public CircuitElement start;
    public CircuitElement end;
    public ArrayList<GridCenterPoint> path;
    public boolean hasPower = false;

    private final String horizontalTexture;
    private final String verticalTexture;

    public WirePath(CircuitElement start, CircuitElement end, ArrayList<GridCenterPoint> path) {
        this.start = start;
        this.end = end;
        this.path = path;
        this.horizontalTexture = getWireTexture(start, false);
        this.verticalTexture = getWireTexture(start, true);
    }

    private String getWireTexture(CircuitElement element, boolean vertical) {
        if (element.color == null) return null;
        return switch (element.color) {
            case RED -> vertical ? "red wire vertical" : "red wire extension";
            case BLUE -> vertical ? "blue wire vertical" : "blue wire extension";
            case GREEN -> vertical ? "green wire vertical" : "green wire extension";
            case YELLOW -> vertical ? "yellow wire vertical" : "yellow wire extension";
        };
    }

    public void draw() {
        if (path.size() < 2) return;

        float extend = path.get(0).tileSize * 0.25f;

        // Draw only intermediate path tiles (skip first and last which are element positions)
        for (int i = 1; i < path.size() - 1; i++) {
            GridCenterPoint tile = path.get(i);

            boolean connectLeft = false;
            boolean connectRight = false;
            boolean connectUp = false;
            boolean connectDown = false;

            // Connect to previous tile
            GridCenterPoint prev = path.get(i - 1);
            connectLeft = prev.gridX < tile.gridX;
            connectRight = prev.gridX > tile.gridX;
            connectDown = prev.gridY < tile.gridY;
            connectUp = prev.gridY > tile.gridY;

            // Connect to next tile
            GridCenterPoint next = path.get(i + 1);
            connectLeft |= next.gridX < tile.gridX;
            connectRight |= next.gridX > tile.gridX;
            connectDown |= next.gridY < tile.gridY;
            connectUp |= next.gridY > tile.gridY;

            // Extension for connection to elements at path boundaries
            float extendLeft = 0;
            float extendRight = 0;
            float extendUp = 0;
            float extendDown = 0;

            // If this is the first wire tile (connects to start element)
            if (i == 1 && start != null) {
                if (connectLeft) extendLeft = extend;
                if (connectRight) extendRight = extend;
                if (connectUp) extendUp = extend;
                if (connectDown) extendDown = extend;
            }

            // If this is the last wire tile (connects to end element)
            if (i == path.size() - 2 && end != null) {
                if (connectLeft) extendLeft = extend;
                if (connectRight) extendRight = extend;
                if (connectUp) extendUp = extend;
                if (connectDown) extendDown = extend;
            }

            drawSegments(tile, connectLeft, connectRight, connectUp, connectDown,
                    extendLeft, extendRight, extendUp, extendDown);
        }
    }

    private void drawSegments(GridCenterPoint tile, boolean left, boolean right, boolean up, boolean down,
                              float extendLeft, float extendRight, float extendUp, float extendDown) {
        float size = tile.tileSize;
        float half = size / 2f;
        float x = tile.centerX - half;
        float y = tile.centerY - half;

        // Horizontal
        if (left) {
            GameApp.drawTexture(horizontalTexture,
                    x - extendLeft,
                    tile.centerY - size * 0.25f,
                    half + extendLeft,
                    size * 0.5f);
        }
        if (right) {
            GameApp.drawTexture(horizontalTexture,
                    tile.centerX,
                    tile.centerY - size * 0.25f,
                    half + extendRight,
                    size * 0.5f);
        }

        // Vertical
        if (up) {
            GameApp.drawTexture(verticalTexture,
                    tile.centerX - size * 0.25f,
                    tile.centerY,
                    size * 0.5f,
                    half + extendUp);
        }
        if (down) {
            GameApp.drawTexture(verticalTexture,
                    tile.centerX - size * 0.25f,
                    y - extendDown,
                    size * 0.5f,
                    half + extendDown);
        }
    }

    public void update() {
        hasPower = start.hasPower;
        if (hasPower) end.hasPower = true;
    }
}