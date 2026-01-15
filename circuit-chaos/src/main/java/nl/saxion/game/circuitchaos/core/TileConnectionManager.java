package nl.saxion.game.circuitchaos.core;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.gameapp.GameApp;

import java.util.*;

public class TileConnectionManager {

    private final ArrayList<WirePath> wirePaths = new ArrayList<>();
    private final ArrayList<GridCenterPoint> currentPath = new ArrayList<>();
    private final Set<String> occupiedTiles = new HashSet<>();
    private final Map<CircuitElement, Integer> connectedElements = new HashMap<>();

    private CircuitElement selectedElement = null;
    private boolean isBuilding = false;

    // Grid parameters
    private float gridX;
    private float gridY;
    private float cellSize;

    // Wire breaking system
    private Random random = new Random();
    private float wireBreakTimer = 0f;
    private float wireBreakInterval = 3f; // Break a wire every 5 seconds

    private float lastClickTime = 0f;
    private final float doubleClickThreshold = 0.25f; // 0.25 seconds for double click


    private int getConnections(CircuitElement e) {
        return connectedElements.getOrDefault(e, 0);
    }

    private boolean canStartConnection(CircuitElement e) {
        return getConnections(e) < e.getMaxConnections();
    }

    public void startBuilding(CircuitElement element, float gridX, float gridY, float cellSize) {
        if (element == null) return;
        if (!canStartConnection(element)) return;

        // Dead bulbs cannot start connections
        if (element instanceof Bulb && !element.hasPower()) return;

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

    public boolean removeWireAt(float mouseX, float mouseY) {
        Iterator<WirePath> iterator = wirePaths.iterator();
        while (iterator.hasNext()) {
            WirePath wire = iterator.next();
            if (wire.containsPoint(mouseX, mouseY, 20f)) { // reuse hitbox check
                // Free up occupied tiles
                for (int i = 1; i < wire.path.size() - 1; i++) {
                    GridCenterPoint tile = wire.path.get(i);
                    occupiedTiles.remove(key((int) tile.gridX, (int) tile.gridY));
                }

                // Decrement connection counts
                connectedElements.put(wire.start, getConnections(wire.start) - 1);
                connectedElements.put(wire.end, getConnections(wire.end) - 1);

                iterator.remove(); // remove the wire
                System.out.println("Wire connection removed!");
                return true;
            }
        }
        return false;
    }

    public void finishBuilding(CircuitElement endElement) {
        if (!isBuilding || endElement == null) return;
        if (endElement == selectedElement) return;
        if (!canStartConnection(endElement)) return;
        if (!canConnect(selectedElement, endElement)) return;
        if (!selectedElement.hasPower() && !endElement.hasPower()) return;

        int[] endPos = getElementGridPos(endElement);
        GridCenterPoint last = currentPath.get(currentPath.size() - 1);

        if (!isAdjacent((int) last.gridX, (int) last.gridY, endPos[0], endPos[1])) return;

        ArrayList<GridCenterPoint> completePath = new ArrayList<>(currentPath);

        GridCenterPoint endTile = new GridCenterPoint(
                endPos[0], endPos[1],
                gridToWorldCenterX(endPos[0]),
                gridToWorldCenterY(endPos[1]),
                cellSize,
                true
        );
        completePath.add(endTile);

        for (int i = 1; i < completePath.size() - 1; i++) {
            GridCenterPoint tile = completePath.get(i);
            occupiedTiles.add(key((int) tile.gridX, (int) tile.gridY));
        }

        connectedElements.put(
                selectedElement,
                getConnections(selectedElement) + 1
        );

        connectedElements.put(
                endElement,
                getConnections(endElement) + 1
        );

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

    public ArrayList<WirePath> getWirePaths() {
        return wirePaths;
    }

    // Wire breaking system - call this with delta time when timer is low
    // In TileConnectionManager
    public void updateWireBreaking(float delta, boolean shouldBreakWires) {
        if (!shouldBreakWires || wirePaths.isEmpty()) {
            // Add debug
            if (shouldBreakWires && wirePaths.isEmpty()) {
                System.out.println("Cannot break wires - no wires exist!");
            }
            return;
        }

        wireBreakTimer += delta;

        // Add debug
        if (wireBreakTimer > wireBreakInterval - 0.1f) {
            System.out.println("Wire break timer: " + wireBreakTimer + " / " + wireBreakInterval);
        }

        if (wireBreakTimer >= wireBreakInterval) {
            wireBreakTimer = 0f;
            breakRandomWire();
        }
    }

    private void breakRandomWire() {
        // Find all non-broken wires
        ArrayList<WirePath> workingWires = new ArrayList<>();
        for (WirePath wire : wirePaths) {
            if (!wire.isBroken()) {
                workingWires.add(wire);
            }
        }

        if (workingWires.isEmpty()) return;

        // Break a random wire
        int index = random.nextInt(workingWires.size());
        workingWires.get(index).breakWire();

        System.out.println("Wire broken! Right-click to repair.");
    }

    // Repair wire at mouse position
    public boolean repairWireAt(float mouseX, float mouseY) {
        for (WirePath wire : wirePaths) {
            if (wire.isBroken() && wire.containsPoint(mouseX, mouseY, 20f)) {
                wire.repair();
                System.out.println("Wire repaired!");
                return true;
            }
        }
        return false;
    }

    // Check if two elements are connected
    public boolean areElementsConnected(CircuitElement a, CircuitElement b) {
        for (WirePath wire : wirePaths) {
            if (!wire.isBroken() && wire.connects(a, b)) {
                return true;
            }
        }
        return false;
    }

    // Count broken wires
    public int getBrokenWireCount() {
        int count = 0;
        for (WirePath wire : wirePaths) {
            if (wire.isBroken()) count++;
        }
        return count;
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    public void reset() {
        cancelBuilding();
        wirePaths.clear();
        occupiedTiles.clear();
        connectedElements.clear();
        wireBreakTimer = 0f;
    }

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
        if ((a instanceof VoltageRegulator && b instanceof VoltagePort && b.color == PortColor.WHITE) ||
                (a instanceof VoltagePort && b instanceof VoltageRegulator && a.color == PortColor.WHITE)) {
            return true;
        }

        if ((a instanceof ExtensionCord && b instanceof PowerPlug) ||
                (a instanceof PowerPlug && b instanceof ExtensionCord)) {
            return true;
        }

        if ((a instanceof Switch && b instanceof Tool) ||
                (a instanceof Tool && b instanceof Switch)) {
            return true;
        }

        if ((a instanceof Tool && b instanceof WirePort) ||
                (a instanceof WirePort && b instanceof Tool)) {
            return a.color == b.color;
        }

        // White ports must NOT connect to each other
        if (a instanceof VoltagePort && b instanceof VoltagePort) {
            return false;
        }

        if (a instanceof VoltagePort && b instanceof VoltageRegulator ||
            a instanceof VoltageRegulator && b instanceof VoltagePort) {
            return true;
        }


        if (a.color == null || b.color == null) return false;
        return a.color == b.color;
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    public boolean hasWireAtCell(int gridX, int gridY) {
        String cellKey = key(gridX, gridY);

        // Check if this cell is in any wire path
        for (WirePath wire : wirePaths) {
            for (GridCenterPoint point : wire.path) {
                if ((int)point.gridX == gridX && (int)point.gridY == gridY) {
                    return true;
                }
            }
        }

        return false;
    }

    // Also check if wire at cell has power (for win condition)
    public boolean isWirePoweredAtCell(int gridX, int gridY) {
        for (WirePath wire : wirePaths) {
            if (wire.isBroken()) continue; // Broken wires have no power

            for (GridCenterPoint point : wire.path) {
                if ((int)point.gridX == gridX && (int)point.gridY == gridY) {
                    return wire.hasPower;
                }
            }
        }

        return false;
    }
}