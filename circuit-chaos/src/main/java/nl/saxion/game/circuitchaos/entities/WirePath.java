package nl.saxion.game.circuitchaos.entities;

import com.badlogic.gdx.graphics.Color;
import nl.saxion.gameapp.GameApp;
import java.util.ArrayList;

public class WirePath {
    public CircuitElement start;
    public CircuitElement end;
    public ArrayList<GridCenterPoint> path;
    public boolean hasPower = false;

    public WirePath(CircuitElement start, CircuitElement end, ArrayList<GridCenterPoint> path) {
        this.start = start;
        this.end = end;
        this.path = new ArrayList<>(path);
    }

    public void draw() {
        Color wireColor = hasPower ? Color.YELLOW : Color.GRAY;

        // Get all wire segments (orthogonal lines)
        ArrayList<WireSegment> segments = buildSegments();

        // Draw each segment
        for (WireSegment seg : segments) {
            GameApp.drawLine(seg.x1, seg.y1, seg.x2, seg.y2, wireColor);
            GameApp.setLineWidth(4);
        }
    }

    private ArrayList<WireSegment> buildSegments() {
        ArrayList<WireSegment> segments = new ArrayList<>();

        // Start point (element center)
        float startX = start.positionX + start.positionWidth / 2;
        float startY = start.positionY + start.positionHeight / 2;

        // End point (element center)
        float endX = end.positionX + end.positionWidth / 2;
        float endY = end.positionY + end.positionHeight / 2;

        if (path.isEmpty()) {
            // Direct connection: draw orthogonal path
            segments.addAll(createOrthogonalPath(startX, startY, endX, endY));
        } else {
            // Connect start to first tile
            GridCenterPoint first = path.getFirst();
            segments.addAll(createOrthogonalPath(startX, startY, first.centerX, first.centerY));

            // Connect tiles to each other
            for (int i = 0; i < path.size() - 1; i++) {
                GridCenterPoint curr = path.get(i);
                GridCenterPoint next = path.get(i + 1);
                segments.addAll(createOrthogonalPath(curr.centerX, curr.centerY, next.centerX, next.centerY));
            }

            // Connect last tile to end
            GridCenterPoint last = path.getLast();
            segments.addAll(createOrthogonalPath(last.centerX, last.centerY, endX, endY));
        }

        return segments;
    }

    // Create orthogonal (only horizontal/vertical) path between two points
    private ArrayList<WireSegment> createOrthogonalPath(float x1, float y1, float x2, float y2) {
        ArrayList<WireSegment> segments = new ArrayList<>();

        // Always go horizontal first, then vertical (or vice versa based on distance)
        float dx = Math.abs(x2 - x1);
        float dy = Math.abs(y2 - y1);

        if (dx > 0.1f) {
            // Horizontal segment
            segments.add(new WireSegment(x1, y1, x2, y1));
            if (dy > 0.1f) {
                // Vertical segment
                segments.add(new WireSegment(x2, y1, x2, y2));
            }
        } else if (dy > 0.1f) {
            // Only vertical segment needed
            segments.add(new WireSegment(x1, y1, x1, y2));
        }

        return segments;
    }

    public void update() {
        hasPower = start.hasPower;
        if (hasPower) {
            end.hasPower = true;
        }
    }
}