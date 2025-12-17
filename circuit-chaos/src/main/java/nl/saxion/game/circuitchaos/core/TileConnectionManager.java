package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TileConnectionManager {

    private final ArrayList<WirePath> wirePaths = new ArrayList<>();
    private final ArrayList<GridCenterPoint> currentPath = new ArrayList<>();
    private final Set<String> occupiedTiles = new HashSet<>();
    private final Set<CircuitElement> connectedElements = new HashSet<>();

    private CircuitElement selectedElement = null;
    private boolean isBuilding = false;

    // Grid parameters
    private float gridX;
    private float gridY;
    private float cellSize;

    // ==== PUBLIC API ====

    public void startBuilding(CircuitElement element, float gridX, float gridY, float cellSize) {
        if (element == null) return;
        if (connectedElements.contains(element)) return;
        if (!element.hasPower) return;

        cancelBuilding();

        this.selectedElement = element;
        this.isBuilding = true;
        this.gridX = gridX;
        this.gridY = gridY;
        this.cellSize = cellSize;

        element.isSelected = true;
        currentPath.clear();

        int[] pos = getElementGridPos(element);
        GridCenterPoint startTile = new GridCenterPoint(
                pos[0], pos[1],
                gridToWorldCenterX(pos[0]),
                gridToWorldCenterY(pos[1]),
                cellSize,
                true
        );
        currentPath.add(startTile);
    }

    public boolean addTileToPath(int gridCol, int gridRow) {
        if (!isBuilding || selectedElement == null) return false;

        String key = key(gridCol, gridRow);
        if (occupiedTiles.contains(key)) return false;

        GridCenterPoint last = currentPath.get(currentPath.size() - 1);

        if (!isAdjacent((int) last.gridX, (int) last.gridY, gridCol, gridRow)) return false;

        boolean isHorizontal = Math.abs(gridCol - last.gridX) > Math.abs(gridRow - last.gridY);

        GridCenterPoint newTile = new GridCenterPoint(
                gridCol, gridRow,
                gridToWorldCenterX(gridCol),
                gridToWorldCenterY(gridRow),
                cellSize,
                isHorizontal
        );

        currentPath.add(newTile);
        return true;
    }

    public void finishBuilding(CircuitElement endElement) {
        if (!isBuilding || endElement == null) return;
        if (endElement == selectedElement) return;
        if (connectedElements.contains(endElement)) return;
        if (!canConnect(selectedElement, endElement)) return;

        int[] endPos = getElementGridPos(endElement);
        GridCenterPoint last = currentPath.get(currentPath.size() - 1);

        if (!isAdjacent((int) last.gridX, (int) last.gridY, endPos[0], endPos[1])) return;

        // Store ALL tiles including start and end positions as the complete path
        ArrayList<GridCenterPoint> completePath = new ArrayList<>(currentPath);

        // Add the end tile to complete the path
        GridCenterPoint endTile = new GridCenterPoint(
                endPos[0], endPos[1],
                gridToWorldCenterX(endPos[0]),
                gridToWorldCenterY(endPos[1]),
                cellSize,
                true
        );
        completePath.add(endTile);

        // Mark only intermediate tiles as occupied (not start/end element positions)
        for (int i = 1; i < completePath.size() - 1; i++) {
            GridCenterPoint tile = completePath.get(i);
            occupiedTiles.add(key((int) tile.gridX, (int) tile.gridY));
        }

        connectedElements.add(selectedElement);
        connectedElements.add(endElement);

        // Pass the complete path to WirePath
        wirePaths.add(new WirePath(selectedElement, endElement, completePath));
        cancelBuilding();
    }

    public void cancelBuilding() {
        if (selectedElement != null) selectedElement.isSelected = false;
        selectedElement = null;
        isBuilding = false;
        currentPath.clear();
    }

    public void drawWirePathsTextures() {
        for (WirePath wire : wirePaths) {
            wire.draw();
        }
    }

    public void drawWirePathsPreview() {
        if (!isBuilding) return;
        Color c = new Color(0, 1, 1, 0.7f);

        for (int i = 0; i < currentPath.size() - 1; i++) {
            GridCenterPoint a = currentPath.get(i);
            GridCenterPoint b = currentPath.get(i + 1);
            GameApp.drawLine(a.centerX, a.centerY, b.centerX, b.centerY, c);
        }
    }

    public void updateWirePaths() {
        for (WirePath wire : wirePaths) {
            wire.update();
        }
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    // ==== PRIVATE HELPERS ====

    private int[] getElementGridPos(CircuitElement e) {
        int gx = Math.round((e.positionX - gridX) / cellSize);
        int gy = Math.round((e.positionY - gridY) / cellSize);
        return new int[]{gx, gy};
    }

    private float gridToWorldCenterX(int col) {
        return gridX + col * cellSize + cellSize / 2f;
    }

    private float gridToWorldCenterY(int row) {
        return gridY + row * cellSize + cellSize / 2f;
    }

    private boolean isAdjacent(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    private boolean canConnect(CircuitElement a, CircuitElement b) {
        // Option 1: only same colors; null cannot connect
        if (a.color == null || b.color == null) return false;
        return a.color == b.color;
        // If you want null to be wildcard instead, use:
        // return a.color == null || b.color == null || a.color == b.color;
    }

    private String key(int x, int y) {
        return x + "," + y;
    }
}