package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import java.util.*;

public class HintManager {
    private boolean hintUsed = false;
    private Random random = new Random();
    private LevelManager levelManager;

    public HintManager(LevelManager levelManager) {
        this.hintUsed = false;
        this.levelManager = levelManager;
    }

    public void reset() {
        hintUsed = false;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }

    public boolean useHint(int level, LevelManager levelManager, TileConnectionManager connectionManager,
                           float gridX, float gridY, float cellSize) {
        if (hintUsed) {
            System.out.println("Hint already used for this level!");
            return false;
        }

        boolean success = solveOneConnection(levelManager, connectionManager, gridX, gridY, cellSize);

        if (success) {
            hintUsed = true;
            System.out.println("Hint used! Connection created automatically.");
        } else {
            System.out.println("No available hint connection found.");
        }

        return success;
    }

    /**
     * Attempts to solve one incomplete connection on the current level
     * Randomly selects from ALL available connection types
     */
    private boolean solveOneConnection(LevelManager levelManager, TileConnectionManager connectionManager,
                                       float gridX, float gridY, float cellSize) {

        // Collect ALL possible connections
        List<ConnectionOption> allOptions = new ArrayList<>();

        // Find all WirePort pairs
        allOptions.addAll(findWirePortConnections(levelManager));

        // Find all Bulb connections
        allOptions.addAll(findBulbConnections(levelManager));

        // Find all PowerPlug to ExtensionCord connections
        allOptions.addAll(findPlugToExtensionConnections(levelManager));

        // Find all VoltagePort connections
        allOptions.addAll(findVoltagePortConnections(levelManager));

        if (allOptions.isEmpty()) {
            System.out.println("No available connections found!");
            return false;
        }

        // Randomly pick one connection to complete
        ConnectionOption chosen = allOptions.get(random.nextInt(allOptions.size()));

        System.out.println("Hint connecting: " + chosen.description);

        return findAndCreatePath(chosen.start, chosen.end, connectionManager, gridX, gridY, cellSize);
    }

    /**
     * Find all possible WirePort connections grouped by color
     */
    private List<ConnectionOption> findWirePortConnections(LevelManager levelManager) {
        List<ConnectionOption> options = new ArrayList<>();
        ArrayList<WirePort> ports = levelManager.getPorts();

        // Group ports by color
        Map<PortColor, List<WirePort>> portsByColor = new HashMap<>();
        for (WirePort port : ports) {
            if (!port.connected) {
                portsByColor.computeIfAbsent(port.color, k -> new ArrayList<>()).add(port);
            }
        }

        // For each color with 2+ unconnected ports, add connection options
        for (Map.Entry<PortColor, List<WirePort>> entry : portsByColor.entrySet()) {
            List<WirePort> colorPorts = entry.getValue();
            if (colorPorts.size() >= 2) {
                WirePort port1 = colorPorts.get(0);
                WirePort port2 = colorPorts.get(1);
                options.add(new ConnectionOption(
                        port1, port2,
                        "WirePort (" + entry.getKey() + ")"
                ));
            }
        }

        return options;
    }

    /**
     * Find all possible Bulb connections
     */
    private List<ConnectionOption> findBulbConnections(LevelManager levelManager) {
        List<ConnectionOption> options = new ArrayList<>();
        ArrayList<Bulb> bulbs = levelManager.getBulbs();

        Bulb litBulb = null;
        Bulb unlitBulb = null;

        // Find pairs of lit and unlit bulbs
        for (Bulb bulb : bulbs) {
            if (bulb.hasPower && !bulb.connected) {
                litBulb = bulb;
                break;
            }
        }

        if (litBulb != null) {
            for (Bulb bulb : bulbs) {
                if (!bulb.hasPower && !bulb.connected && bulb.color == litBulb.color) {
                    unlitBulb = bulb;
                    break;
                }
            }
        }

        if (litBulb != null && unlitBulb != null) {
            options.add(new ConnectionOption(
                    litBulb, unlitBulb,
                    "Bulb (YELLOW)"
            ));
        }

        return options;
    }

    /**
     * Find all PowerPlug to ExtensionCord connections
     */
    private List<ConnectionOption> findPlugToExtensionConnections(LevelManager levelManager) {
        List<ConnectionOption> options = new ArrayList<>();
        ArrayList<PowerPlug> plugs = levelManager.getPlugs();
        ArrayList<ExtensionCord> cords = levelManager.getExtensionCords();

        // Find all unconnected plug-cord pairs
        for (PowerPlug plug : plugs) {
            if (!plug.connected) {
                for (ExtensionCord cord : cords) {
                    if (!cord.connected) {
                        options.add(new ConnectionOption(
                                plug, cord,
                                "PowerPlug to ExtensionCord"
                        ));
                        // Only return first valid pair to avoid duplicate hints
                        return options;
                    }
                }
            }
        }

        return options;
    }

    /**
     * Find all VoltagePort connections
     */
    private List<ConnectionOption> findVoltagePortConnections(LevelManager levelManager) {
        List<ConnectionOption> options = new ArrayList<>();
        ArrayList<VoltagePort> voltagePorts = levelManager.getVoltagePorts();

        // Find pairs of unconnected voltage ports
        List<VoltagePort> unconnected = new ArrayList<>();
        for (VoltagePort port : voltagePorts) {
            if (!port.connected) {
                unconnected.add(port);
            }
        }

        // Create connection options for pairs
        if (unconnected.size() >= 2) {
            VoltagePort port1 = unconnected.get(0);
            VoltagePort port2 = unconnected.get(1);
            options.add(new ConnectionOption(
                    port1, port2,
                    "VoltagePort"
            ));
        }

        return options;
    }

    /**
     * Uses simple pathfinding to create a connection between two entities
     */
    private boolean findAndCreatePath(CircuitElement start, CircuitElement end, TileConnectionManager connectionManager,
                                      float gridX, float gridY, float cellSize) {

        float startX = getPositionX(start);
        float startY = getPositionY(start);
        float endX = getPositionX(end);
        float endY = getPositionY(end);

        int startGridX = Math.round((startX - gridX) / cellSize);
        int startGridY = Math.round((startY - gridY) / cellSize);
        int endGridX = Math.round((endX - gridX) / cellSize);
        int endGridY = Math.round((endY - gridY) / cellSize);

        List<GridPoint> path = findPath(startGridX, startGridY, endGridX, endGridY, gridX, gridY, cellSize);

        if (path == null || path.isEmpty()) {
            return false;
        }

        connectionManager.startBuilding(start, gridX, gridY, cellSize);

        for (int i = 1; i < path.size() - 1; i++) {
            GridPoint point = path.get(i);
            connectionManager.addTileToPath(point.x, point.y);
        }

        connectionManager.finishBuilding(end);

        return true;
    }

    /**
     * A* pathfinding that avoids occupied cells
     */
    private List<GridPoint> findPath(int startX, int startY, int endX, int endY,
                                     float gridX, float gridY, float cellSize) {

        PriorityQueue<PathNode> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        Map<String, PathNode> allNodes = new HashMap<>();

        PathNode startNode = new PathNode(startX, startY, null, 0, manhattanDistance(startX, startY, endX, endY));
        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);

        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            String currentKey = current.x + "," + current.y;

            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            closedSet.add(currentKey);

            // Check all 4 directions (up, down, left, right)
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                String neighborKey = newX + "," + newY;

                // Skip if already evaluated
                if (closedSet.contains(neighborKey)) {
                    continue;
                }

                // Skip if occupied (but allow start and end positions)
                boolean isStartOrEnd = (newX == startX && newY == startY) || (newX == endX && newY == endY);
                if (!isStartOrEnd && levelManager.isCellOccupied(newX, newY, gridX, gridY, cellSize)) {
                    continue;
                }

                float newG = current.g + 1;
                float newH = manhattanDistance(newX, newY, endX, endY);
                float newF = newG + newH;

                PathNode neighbor = allNodes.get(neighborKey);
                if (neighbor == null) {
                    neighbor = new PathNode(newX, newY, current, newG, newH);
                    allNodes.put(neighborKey, neighbor);
                    openSet.add(neighbor);
                } else if (newG < neighbor.g) {
                    neighbor.g = newG;
                    neighbor.f = newF;
                    neighbor.parent = current;
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        // No path found, return null
        return null;
    }

    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<GridPoint> reconstructPath(PathNode endNode) {
        List<GridPoint> path = new ArrayList<>();
        PathNode current = endNode;

        while (current != null) {
            path.add(0, new GridPoint(current.x, current.y));
            current = current.parent;
        }

        return path;
    }

    private float getPositionX(Object entity) {
        if (entity instanceof WirePort) return ((WirePort) entity).positionX;
        if (entity instanceof Bulb) return ((Bulb) entity).positionX;
        if (entity instanceof PowerPlug) return ((PowerPlug) entity).positionX;
        if (entity instanceof ExtensionCord) return ((ExtensionCord) entity).positionX;
        if (entity instanceof VoltagePort) return ((VoltagePort) entity).positionX;
        return 0;
    }

    private float getPositionY(Object entity) {
        if (entity instanceof WirePort) return ((WirePort) entity).positionY;
        if (entity instanceof Bulb) return ((Bulb) entity).positionY;
        if (entity instanceof PowerPlug) return ((PowerPlug) entity).positionY;
        if (entity instanceof ExtensionCord) return ((ExtensionCord) entity).positionY;
        if (entity instanceof VoltagePort) return ((VoltagePort) entity).positionY;
        return 0;
    }

    /**
     * Represents a possible connection between two entities
     */
    private static class ConnectionOption {
        CircuitElement start;
        CircuitElement end;
        String description;

        ConnectionOption(CircuitElement start, CircuitElement end, String description) {
            this.start = start;
            this.end = end;
            this.description = description;
        }
    }

    /**
     * Simple grid coordinate class
     */
    private static class GridPoint {
        int x, y;

        GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Node used for A* pathfinding
     */
    private static class PathNode implements Comparable<PathNode> {
        int x, y;
        PathNode parent;
        float g; // Cost from start
        float h; // Heuristic to end
        float f; // Total cost (g + h)

        PathNode(int x, int y, PathNode parent, float g, float h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public int compareTo(PathNode other) {
            return Float.compare(this.f, other.f);
        }
    }
}