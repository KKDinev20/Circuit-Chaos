package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.game.circuitchaos.util.GameConstants;

import java.util.ArrayList;

public class LevelManager {
    private ArrayList<Bulb> bulbs = new ArrayList<>();
    private ArrayList<WirePort> ports = new ArrayList<>();
    private static ArrayList<ExtensionCord> extensionCords = new ArrayList<>();
    private static ArrayList<PowerPlug> plugs = new ArrayList<>();
    public static int currentLevel = 1;
    private boolean initialized = false;

    public void generateLevelOne(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
        extensionCords.clear();
        plugs.clear();

        // Bulb 1: At grid position (2, 4) - powered (lit)
        Bulb bulb1 = new Bulb(gridX + (2 * cellSize), gridY + (4 * cellSize), cellSize);
        bulb1.color = PortColor.YELLOW;
        bulb1.hasPower = true;
        bulb1.update();

        // Bulb 2: At grid position (3, 0) - not powered (unlit)
        Bulb bulb2 = new Bulb(gridX + (3 * cellSize), gridY, cellSize);
        bulb2.color = PortColor.YELLOW;
        bulb2.hasPower = false;
        bulb2.update();

        // Port 1: position (0, 2)
        WirePort port1 = new WirePort(gridX, gridY + (2 * cellSize), cellSize, PortColor.RED);
        port1.update();

        // Port 2: position (2,0)
        WirePort port2 = new WirePort(gridX + (2 * cellSize), gridY, cellSize, PortColor.RED);
        port2.update();

        // Port 3: position (0, 5)
        WirePort port3 = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.BLUE);
        port3.update();

        // Port 4: position (4, 4)
        WirePort port4 = new WirePort(gridX + (4 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.BLUE);
        port4.update();

        bulbs.add(bulb1);
        bulbs.add(bulb2);
        ports.add(port1);
        ports.add(port2);
        ports.add(port3);
        ports.add(port4);
    }

    public void generateLevelTwo(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
        extensionCords.clear();
        plugs.clear();

        ExtensionCord extensionCord = new ExtensionCord(gridX + (2 * cellSize), gridY + (4 * cellSize), cellSize);
        extensionCord.update();
        extensionCords.add(extensionCord);

        PowerPlug plug1 = new PowerPlug(gridX + (1 * cellSize), gridY + (3 * cellSize), cellSize);
        plug1.update();
        plugs.add(plug1);

        PowerPlug plug2 = new PowerPlug(gridX + (4 * cellSize), gridY + (2 * cellSize), cellSize);
        plug2.update();
        plugs.add(plug2);

        // Port 1A
        WirePort port1A = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.BLUE);
        port1A.update();
        ports.add(port1A);

        // Port 1B
        WirePort port1B = new WirePort(gridX + (2 * cellSize), gridY + (2 * cellSize), cellSize, PortColor.BLUE);
        port1B.update();
        ports.add(port1B);

        // Port 2A
        WirePort port2A = new WirePort(gridX, gridY, cellSize, PortColor.GREEN);
        port2A.update();
        ports.add(port2A);

        // Port 2B
        WirePort port2B = new WirePort(gridX + (2 * cellSize), gridY + cellSize, cellSize, PortColor.GREEN);
        port2B.update();
        ports.add(port2B);

        // Port 3A
        WirePort port3A = new WirePort(gridX + (5 * cellSize), gridY + (3 * cellSize), cellSize, PortColor.RED);
        port3A.update();
        ports.add(port3A);

        // Port 3B
        WirePort port3B = new WirePort(gridX + (4 * cellSize), gridY + (5 * cellSize), cellSize, PortColor.RED);
        port3B.update();
        ports.add(port3B);
    }

    public void generateLevelThree(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
    }

    public void generateLevelFour(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
    }

    public void generateLevelFive(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
    }

    public void generateLevelSix(float gridX, float gridY, float cellSize) {
        bulbs.clear();
        ports.clear();
    }

    public void initializeLevel(float gridX, float gridY, float gridWidth) {
        if (!initialized) {
            float cellSize = gridWidth / GameConstants.GRID_SIZE;

            switch(currentLevel) {
                case 1:
                    generateLevelOne(gridX, gridY, cellSize);
                    break;
                case 2:
                    generateLevelTwo(gridX, gridY, cellSize);
                    break;
                case 3:
                    generateLevelThree(gridX, gridY,cellSize);
                    break;
                case 4:
                    generateLevelFour(gridX, gridY,cellSize);
                    break;
                case 5:
                    generateLevelFive(gridX, gridY,cellSize);
                    break;
                case 6:
                    generateLevelSix(gridX, gridY,cellSize);
                    break;
                default:
                    generateLevelOne(gridX, gridY, cellSize); // Default
            }
            initialized = true;
        }
    }

    public void resetLevel() {
        initialized = false;
        bulbs.clear();
        ports.clear();
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
        for (ExtensionCord cord : extensionCords) {
            cord.draw();
        }
        for (PowerPlug plug : plugs) {
            plug.draw();
        }
    }

    // For checking if cells are occupied
    public boolean isCellOccupied(int gridX, int gridY, float actualGridX, float actualGridY, float cellSize) {
        float cellScreenX = actualGridX + (gridX * cellSize);
        float cellScreenY = actualGridY + (gridY * cellSize);

        for (Bulb bulb : bulbs) {
            if (rectanglesOverlap(cellScreenX, cellScreenY, cellSize, cellSize,
                    bulb.positionX, bulb.positionY, bulb.positionWidth, bulb.positionHeight)) {
                return true;
            }
        }

        for (WirePort port : ports) {
            if (rectanglesOverlap(cellScreenX, cellScreenY, cellSize, cellSize,
                    port.positionX, port.positionY, port.positionWidth, port.positionHeight)) {
                return true;
            }
        }

        return false;
    }

    private boolean rectanglesOverlap(float x1, float y1, float w1, float h1,
                                      float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }

    public ArrayList<Bulb> getBulbs() {
        return bulbs;
    }

    public ArrayList<WirePort> getPorts() {
        return ports;
    }

    public static ArrayList<ExtensionCord> getExtensionCords() {
        return extensionCords;
    }

    public static ArrayList<PowerPlug> getPlugs() {
        return plugs;
    }
}