package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.game.circuitchaos.util.GameConstants;
import java.util.ArrayList;

public class LevelManager {
    private ArrayList<Bulb> bulbs = new ArrayList<>();
    private ArrayList<WirePort> ports = new ArrayList<>();
    private int currentLevel = 1;
    private boolean initialized = false;

    public void generateLevelOne(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();

        // Bulb 1: At grid position (1, 1) - powered (lit)
        Bulb bulb1 = new Bulb(gridX + (2 * cellSize), gridY + (4 * cellSize), cellSize);
        bulb1.hasPower = true;
        bulb1.update();

        // Bulb 2: At grid position (4, 1) - not powered (unlit)
        Bulb bulb2 = new Bulb(gridX + (3 * cellSize), gridY + (0 * cellSize), cellSize);
        bulb2.hasPower = false;
        bulb2.update();

        WirePort port1 = new WirePort(gridX, gridY + (2 * cellSize), cellSize, PortColor.RED);
        port1.update();

        WirePort port2 = new WirePort(gridX + (2 * cellSize), gridY, cellSize, PortColor.RED);
        port2.update();

        WirePort port3 = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.BLUE);
        port3.update();

        WirePort port4 = new WirePort(gridX + (4 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.BLUE);
        port4.update();

        bulbs.add(bulb1);
        bulbs.add(bulb2);
        ports.add(port1);
        ports.add(port2);
        ports.add(port3);
        ports.add(port4);
    }

    // Call this from YourGameScreen.render()
    public void initializeLevel(float gridX, float gridY, float gridWidth) {
        if (!initialized) {
            float cellSize = gridWidth / GameConstants.GRID_SIZE;
            generateLevelOne(gridX, gridY, cellSize);
            initialized = true;
        }
    }

    public void updateElements() {
        for (Bulb bulb : bulbs) {
            bulb.update();
        }
    }

    public void drawElements() {
        for (Bulb bulb : bulbs) {
            bulb.draw();
        }
        for (WirePort port : ports) {
            port.draw();
        }
    }

    // For checking if cells are occupied
    public boolean isCellOccupied(int gridX, int gridY, float actualGridX, float actualGridY, float cellSize) {
        for (Bulb bulb : bulbs) {
            int bulbGridX = (int)((bulb.positionX - actualGridX) / cellSize);
            int bulbGridY = (int)((bulb.positionY - actualGridY) / cellSize);
            if (bulbGridX == gridX && bulbGridY == gridY) {
                return true;
            }
        }
        return false;
    }
}