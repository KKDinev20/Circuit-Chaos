package nl.saxion.game.circuitchaos.entities;

import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.Random;

public class WirePath {

    public CircuitElement start;
    public CircuitElement end;
    public ArrayList<GridCenterPoint> path;
    public boolean hasPower = false;
    private boolean broken = false;
    private float brokenBlinkTimer = 0f;
    private int brokenSegmentIndex = -1; // Which segment is broken
    private Random random = new Random();

    private final String horizontalTexture;
    private final String verticalTexture;
    private final String horizontalBrokenTexture;
    private final String verticalBrokenTexture;

    public WirePath(CircuitElement start, CircuitElement end, ArrayList<GridCenterPoint> path) {
        this.start = start;
        this.end = end;
        this.path = path;
        this.horizontalTexture = getWireTexture(start, false, false);
        this.verticalTexture = getWireTexture(start, true, false);
        this.horizontalBrokenTexture = getWireTexture(start, false, true);
        this.verticalBrokenTexture = getWireTexture(start, true, true);
    }

    private String getWireTexture(CircuitElement element, boolean vertical, boolean broken) {
        if (element.color == null) return null;

        String colorName = element.color.toString().toLowerCase();
        String orientation = vertical ? " wire vertical" : " wire extension";

        if (broken) {
            return "black" + orientation; // Black variant for broken wires
        } else {
            return colorName + orientation;
        }
    }

    public void draw() {
        if (path.size() < 2) return;

        float extend = path.getFirst().tileSize * 0.25f;

        // Draw only intermediate path tiles (skip first and last which are element positions)
        for (int i = 1; i < path.size() - 1; i++) {
            GridCenterPoint tile = path.get(i);

            // Check if this segment is broken and should blink
            boolean isThisSegmentBroken = broken && (i == brokenSegmentIndex);
            boolean shouldHide = false;

            if (isThisSegmentBroken) {
                brokenBlinkTimer += 0.016f;
                float blinkSpeed = 4f;
                shouldHide = (int)(brokenBlinkTimer * blinkSpeed) % 2 == 0;

                if (shouldHide) {
                    continue; // Skip drawing this segment
                }
            }

            boolean connectLeft;
            boolean connectRight;
            boolean connectUp;
            boolean connectDown;

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
                    extendLeft, extendRight, extendUp, extendDown, isThisSegmentBroken);
        }
    }

    private void drawSegments(GridCenterPoint tile, boolean left, boolean right, boolean up, boolean down,
                              float extendLeft, float extendRight, float extendUp, float extendDown,
                              boolean useBrokenTexture) {
        float size = tile.tileSize;
        float half = size / 2f;
        float x = tile.centerX - half;
        float y = tile.centerY - half;

        // Select texture based on broken state
        String hTexture = useBrokenTexture ? horizontalBrokenTexture : horizontalTexture;
        String vTexture = useBrokenTexture ? verticalBrokenTexture : verticalTexture;

        // Horizontal
        if (left) {
            GameApp.drawTexture(hTexture,
                    x - extendLeft,
                    tile.centerY - size * 0.25f,
                    half + extendLeft,
                    size * 0.5f);
        }
        if (right) {
            GameApp.drawTexture(hTexture,
                    tile.centerX,
                    tile.centerY - size * 0.25f,
                    half + extendRight,
                    size * 0.5f);
        }

        // Vertical
        if (up) {
            GameApp.drawTexture(vTexture,
                    tile.centerX - size * 0.25f,
                    tile.centerY,
                    size * 0.5f,
                    half + extendUp);
        }
        if (down) {
            GameApp.drawTexture(vTexture,
                    tile.centerX - size * 0.25f,
                    y - extendDown,
                    size * 0.5f,
                    half + extendDown);
        }
    }

    public void update() {
        // Only transfer power if wire is not broken
        if (!broken) {
            hasPower = start.hasPower;
            if (hasPower) end.hasPower = true;
        } else {
            // Broken wire doesn't transfer power
            hasPower = false;
        }
    }

    // Wire breaking methods
    public void breakWire() {
        broken = true;
        brokenBlinkTimer = 0f;
        hasPower = false;

        // Pick a random segment to break (not first or last, those are element positions)
        if (path.size() > 2) {
            brokenSegmentIndex = 1 + random.nextInt(path.size() - 2);
        }

        // Disconnect power from end element
        if (end instanceof Bulb) {
            end.hasPower = false;
        }
    }

    public void repair() {
        broken = false;
        brokenBlinkTimer = 0f;
        brokenSegmentIndex = -1;

        // Restore power if start has power
        if (start.hasPower) {
            hasPower = true;
            end.hasPower = true;
        }
    }

    public boolean isBroken() {
        return broken;
    }

    public boolean connects(CircuitElement a, CircuitElement b) {
        return (start == a && end == b) || (start == b && end == a);
    }

    public boolean containsPoint(float x, float y, float tolerance) {
        // Check if point is near any segment of the wire path
        for (GridCenterPoint point : path) {
            float dx = x - point.centerX;
            float dy = y - point.centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance <= tolerance) {
                return true;
            }
        }
        return false;
    }
}