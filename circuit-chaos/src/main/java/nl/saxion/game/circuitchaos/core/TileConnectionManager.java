package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.gameapp.GameApp;
import java.util.*;

public class TileConnectionManager {
    private ArrayList<WirePath> wirePaths = new ArrayList<>();
    private CircuitElement selectedElement = null;
    private ArrayList<GridCenterPoint> currentPath = new ArrayList<>();
    private Set<String> occupiedTiles = new HashSet<>();
    private Set<CircuitElement> connectedElements = new HashSet<>(); // Track connected elements
    private boolean isBuilding = false;
    private float gridX, gridY, cellSize;

    public void startBuilding(CircuitElement element, float gridX, float gridY, float cellSize) {
        // Check if element is already connected
        if (connectedElements.contains(element)) {
            System.out.println("Element already connected - cannot reuse");
            return;
        }

        // Check if element has power (only for ports with colors)
        if (element.color != null && !element.hasPower) {
            System.out.println("Port has no power - cannot connect");
            return;
        }

        cancelBuilding();

        this.selectedElement = element;
        this.isBuilding = true;
        this.gridX = gridX;
        this.gridY = gridY;
        this.cellSize = cellSize;

        element.isSelected = true;
        currentPath.clear();
    }

    public boolean addTileToPath(float gridX, float gridY, float centerX, float centerY) {
        if (!isBuilding || selectedElement == null) return false;

        // Check if tile is occupied by existing wires
        String tileKey = (int)gridX + "," + (int)gridY;
        if (occupiedTiles.contains(tileKey)) {
            System.out.println("Tile occupied by another wire");
            return false;
        }

        // Check if already in current path
        for (GridCenterPoint tile : currentPath) {
            if (Math.abs(tile.gridX - gridX) < 0.1f && Math.abs(tile.gridY - gridY) < 0.1f) {
                return false;
            }
        }

        GridCenterPoint newTile = new GridCenterPoint(gridX, gridY, centerX, centerY);

        if (currentPath.isEmpty()) {
            // First tile must be adjacent to element
            if (isAdjacent(getElementGridPos(selectedElement), new float[]{gridX, gridY})) {
                currentPath.add(newTile);
                return true;
            }
        } else {
            // Must be adjacent to last tile
            GridCenterPoint last = currentPath.get(currentPath.size() - 1);
            if (isAdjacent(new float[]{last.gridX, last.gridY}, new float[]{gridX, gridY})) {
                currentPath.add(newTile);
                return true;
            }
        }
        return false;
    }

    public void finishBuilding(CircuitElement endElement) {
        if (!isBuilding || selectedElement == null || endElement == selectedElement) {
            return;
        }

        // Check if end element is already connected
        if (connectedElements.contains(endElement)) {
            System.out.println("End element already connected");
            return;
        }

        // Check if same type AND same color
        if (!canConnect(selectedElement, endElement)) {
            System.out.println("Cannot connect: different types or colors");
            return;
        }

        // Check if this connection already exists (prevent duplicate paths)
        if (connectionExists(selectedElement, endElement)) {
            System.out.println("Connection already exists between these elements");
            return;
        }

        // Check if end element is adjacent to path
        float[] endPos = getElementGridPos(endElement);
        boolean isValid = false;

        if (currentPath.isEmpty()) {
            // Direct connection
            isValid = isAdjacent(getElementGridPos(selectedElement), endPos);
        } else {
            GridCenterPoint last = currentPath.get(currentPath.size() - 1);
            isValid = isAdjacent(new float[]{last.gridX, last.gridY}, endPos);
        }

        if (!isValid) {
            System.out.println("End element not adjacent to path");
            return;
        }

        // Mark tiles as occupied
        for (GridCenterPoint tile : currentPath) {
            String key = (int)tile.gridX + "," + (int)tile.gridY;
            occupiedTiles.add(key);
        }

        // Mark elements as connected (locked)
        connectedElements.add(selectedElement);
        connectedElements.add(endElement);

        // Create wire
        wirePaths.add(new WirePath(selectedElement, endElement, currentPath));

        System.out.println("Connection successful! Elements locked.");
        cancelBuilding();
    }

    public void cancelBuilding() {
        if (selectedElement != null) {
            selectedElement.isSelected = false;
        }
        selectedElement = null;
        isBuilding = false;
        currentPath.clear();
    }

    public void drawWirePaths() {
        // Draw completed wires
        for (WirePath wire : wirePaths) {
            wire.draw();
        }

        // Draw preview
        if (isBuilding && selectedElement != null) {
            drawPreview();
        }
    }

    private void drawPreview() {
        // Todo make the wire colored as the same color of the port
        Color previewColor = new Color(0, 1, 1, 0.7f);

        float prevX = selectedElement.positionX + selectedElement.positionWidth / 2;
        float prevY = selectedElement.positionY + selectedElement.positionHeight / 2;

        for (GridCenterPoint tile : currentPath) {
            GameApp.drawLine(
                    prevX,
                    prevY,
                    tile.centerX,
                    tile.centerY,
                    previewColor
            );

            prevX = tile.centerX;
            prevY = tile.centerY;
        }
    }


    public void updateWirePaths() {
        for (WirePath wire : wirePaths) {
            wire.update();
        }
    }

    private float[] getElementGridPos(CircuitElement element) {
        float x = (element.positionX - gridX) / cellSize;
        float y = (element.positionY - gridY) / cellSize;
        return new float[]{x, y};
    }

    private boolean isAdjacent(float[] pos1, float[] pos2) {
        float dx = Math.abs(pos1[0] - pos2[0]);
        float dy = Math.abs(pos1[1] - pos2[1]);
        // Adjacent = exactly 1 tile away in one direction
        return (Math.abs(dx - 1) < 0.1f && dy < 0.1f) ||
                (dx < 0.1f && Math.abs(dy - 1) < 0.1f);
    }

    private boolean canConnect(CircuitElement a, CircuitElement b) {
        // Both must be same type (both bulbs OR both ports)
        boolean sameType = (a.color == null && b.color == null) ||
                (a.color != null && b.color != null);

        if (!sameType) return false;

        // If ports, must have same color
        if (a.color != null && b.color != null) {
            if (a.color != b.color) {
                System.out.println("Port color mismatch: " + a.color + " vs " + b.color);
                return false;
            }
        }

        return true;
    }

    private boolean connectionExists(CircuitElement a, CircuitElement b) {
        for (WirePath wire : wirePaths) {
            if ((wire.start == a && wire.end == b) || (wire.start == b && wire.end == a)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBuilding() {
        return isBuilding;
    }
}