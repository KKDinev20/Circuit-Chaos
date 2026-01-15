package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import nl.saxion.game.circuitchaos.util.GameConstants;

import java.util.ArrayList;


public class LevelManager {
    public static int currentLevel = 1;
    private static ArrayList<ExtensionCord> extensionCords = new ArrayList<>();
    private static ArrayList<PowerPlug> plugs = new ArrayList<>();
    private static ArrayList<VoltageRegulator> regulators = new ArrayList<>();
    private static ArrayList<VoltagePort> voltagePorts = new ArrayList<>();
    private ArrayList<Bulb> bulbs = new ArrayList<>();
    private ArrayList<WirePort> ports = new ArrayList<>();
    private ArrayList<Switch> switches = new ArrayList<>();
    private boolean initialized = false;

    public static ArrayList<ExtensionCord> getExtensionCords() {
        return extensionCords;
    }

    public static ArrayList<PowerPlug> getPlugs() {
        return plugs;
    }

    public static ArrayList<VoltageRegulator> getRegulators() {
        return regulators;
    }

    public static ArrayList<VoltagePort> getVoltagePorts() {
        return voltagePorts;
    }

    public void generateLevelOne(float gridX, float gridY, float cellSize) {
        clearElements();

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
        clearElements();

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
        clearElements();

        // Port 1A
        WirePort port1A = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.GREEN);
        port1A.update();
        ports.add(port1A);

        // Port 1B
        WirePort port1B = new WirePort(gridX + (3 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.GREEN);
        port1B.update();
        ports.add(port1B);

        // Port 2A
        WirePort port2A = new WirePort(gridX, gridY + (2 * cellSize), cellSize, PortColor.RED);
        port2A.update();
        ports.add(port2A);

        // Port 2B
        WirePort port2B = new WirePort(gridX + cellSize, gridY + (4 * cellSize), cellSize, PortColor.RED);
        port2B.update();
        ports.add(port2B);

        // Port 3A
        WirePort port3A = new WirePort(gridX + cellSize, gridY + (2 * cellSize), cellSize, PortColor.ORANGE);
        port3A.update();
        ports.add(port3A);

        // Port 3B
        WirePort port3B = new WirePort(gridX + (3 * cellSize), gridY + cellSize, cellSize, PortColor.ORANGE);
        port3B.update();
        ports.add(port3B);

        Switch switch1 = new Switch(gridX + (2 * cellSize), gridY + (3 * cellSize), cellSize, PortColor.BLACK);
        switch1.hasPower = true; // Switches are connected to power
        switch1.update();
        switches.add(switch1);
    }

    public void generateLevelFour(float gridX, float gridY, float cellSize) {
        clearElements();

        // PORT 1A
        WirePort port1A = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.GREEN);
        port1A.update();
        ports.add(port1A);

        // PORT 1B
        WirePort port1B = new WirePort(gridX + (4 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.GREEN);
        port1B.update();
        ports.add(port1B);

        // PORT 2A
        WirePort port2A = new WirePort(gridX, gridY + (4 * cellSize), cellSize, PortColor.PINK);
        port2A.update();
        ports.add(port2A);

        // PORT 2B
        WirePort port2B = new WirePort(gridX + (1 * cellSize), gridY, cellSize, PortColor.PINK);
        port2B.update();
        ports.add(port2B);

        // PORT 3A
        WirePort port3A = new WirePort(gridX + (1 * cellSize), gridY + (1 * cellSize), cellSize, PortColor.RED);
        port3A.update();
        ports.add(port3A);

        // PORT 3B
        WirePort port3B = new WirePort(gridX + (2 * cellSize), gridY, cellSize, PortColor.RED);
        port3B.update();
        ports.add(port3B);

        // PORT 4A
        WirePort port4A = new WirePort(gridX + (1 * cellSize), gridY + (3 * cellSize), cellSize, PortColor.PURPLE);
        port4A.update();
        ports.add(port4A);

        // PORT 4B
        WirePort port4B = new WirePort(gridX + (3 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.PURPLE);
        port4B.update();
        ports.add(port4B);

        //BULB 1A
        Bulb bulb1 = new Bulb(gridX + (1 * cellSize), gridY + (2 * cellSize), cellSize);
        bulb1.color = PortColor.YELLOW;
        bulb1.hasPower = true;
        bulb1.update();

        bulbs.add(bulb1);

        // BULB 1B
        Bulb bulb2 = new Bulb(gridX + (1 * cellSize) + (3 * cellSize), gridY, cellSize);
        bulb2.color = PortColor.YELLOW;
        bulb2.hasPower = false;
        bulb2.update();

        bulbs.add(bulb2);

        ExtensionCord extensionCord = new ExtensionCord(gridX + (4 * cellSize), gridY + (2 * cellSize), cellSize);
        extensionCord.update();
        extensionCords.add(extensionCord);

        PowerPlug plug1 = new PowerPlug(gridX + (5 * cellSize), gridY + (5 * cellSize), cellSize);
        plug1.update();
        plugs.add(plug1);

        PowerPlug plug2 = new PowerPlug(gridX + (5 * cellSize), gridY + cellSize, cellSize);
        plug2.update();
        plugs.add(plug2);

    }

    public void generateLevelFive(float gridX, float gridY, float cellSize) {
        clearElements();

        float centerOffset = (cellSize * 0.5f) / 2f;

        // Port 1A
        WirePort port1A = new WirePort(gridX, gridY + (5 * cellSize), cellSize, PortColor.RED);
        port1A.update();
        ports.add(port1A);

        // Port 1B
        WirePort port1B = new WirePort(gridX + (1 * cellSize), gridY + (3 * cellSize), cellSize, PortColor.RED);
        port1B.update();
        ports.add(port1B);

        // Port 2A
        WirePort port2A = new WirePort(gridX + (1 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.BLUE);
        port2A.update();
        ports.add(port2A);

        // Port 2B
        WirePort port2B = new WirePort(gridX + (4 * cellSize), gridY + (5 * cellSize), cellSize, PortColor.BLUE);
        port2B.update();
        ports.add(port2B);

        // Port 3A
        WirePort port3A = new WirePort(gridX, gridY, cellSize, PortColor.GREEN);
        port3A.update();
        ports.add(port3A);

        // Port 3B
        WirePort port3B = new WirePort(gridX + (2 * cellSize), gridY + (1 * cellSize), cellSize, PortColor.GREEN);
        port3B.update();
        ports.add(port3B);

        // Voltage regulator
        VoltageRegulator voltageRegulator = new VoltageRegulator(gridX + (4 * cellSize), gridY + (2 * cellSize), cellSize, PortColor.WHITE);
        System.out.println("Placed VoltageRegulator at " + voltageRegulator.positionX + "," + voltageRegulator.positionY);
        voltageRegulator.update();
        regulators.add(voltageRegulator);

        // Port 1 Voltage regulator
        VoltagePort voltagePort1 = new VoltagePort(gridX + (5 * cellSize) + centerOffset, gridY + (5 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort1.update();
        voltagePorts.add(voltagePort1);

        // Port 2 Voltage regulator
        VoltagePort voltagePort2 = new VoltagePort(gridX + (2 * cellSize) + centerOffset, gridY + (3 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort2.update();
        voltagePorts.add(voltagePort2);

        // Port 3 Voltage regulator
        VoltagePort voltagePort3 = new VoltagePort(gridX + (3 * cellSize) + centerOffset, gridY + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort3.update();
        voltagePorts.add(voltagePort3);

        // Port 4 Voltage regulator
        VoltagePort voltagePort4 = new VoltagePort(gridX + centerOffset, gridY + (1 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort4.update();
        voltagePorts.add(voltagePort4);

    }

    public void generateLevelSix(float gridX, float gridY, float cellSize) {
        clearElements();

        float centerOffset = (cellSize * 0.5f) / 2f;

        // Port 1A
        WirePort port1A = new WirePort(gridX, gridY, cellSize, PortColor.RED);
        port1A.update();
        ports.add(port1A);

        // Port 1B
        WirePort port1B = new WirePort(gridX + (3 * cellSize), gridY + (1 * cellSize), cellSize, PortColor.RED);
        port1B.update();
        ports.add(port1B);

        //BULB 1A
        Bulb bulb1 = new Bulb(gridX + (5 * cellSize), gridY + (5 * cellSize), cellSize);
        bulb1.color = PortColor.YELLOW;
        bulb1.hasPower = true;
        bulb1.update();

        bulbs.add(bulb1);

        // BULB 1B
        Bulb bulb2 = new Bulb(gridX + (3 * cellSize) , gridY+ (4 * cellSize), cellSize);
        bulb2.color = PortColor.YELLOW;
        bulb2.hasPower = false;
        bulb2.update();

        bulbs.add(bulb2);

        // EXTENSION CORD
        ExtensionCord extensionCord = new ExtensionCord(gridX, gridY + (3 * cellSize), cellSize);
        extensionCord.update();
        extensionCords.add(extensionCord);

        // PLUG 1A
        PowerPlug plug1 = new PowerPlug(gridX + (1 * cellSize), gridY + (5 * cellSize), cellSize);
        plug1.update();
        plugs.add(plug1);

        // PLUG 1B
        PowerPlug plug2 = new PowerPlug(gridX + (1 * cellSize), gridY + (1 * cellSize), cellSize);
        plug2.update();
        plugs.add(plug2);

        // Voltage regulator
        VoltageRegulator voltageRegulator = new VoltageRegulator(gridX + (4 * cellSize), gridY + (2 * cellSize), cellSize, PortColor.WHITE);
        System.out.println("Placed VoltageRegulator at " + voltageRegulator.positionX + "," + voltageRegulator.positionY);
        voltageRegulator.update();
        regulators.add(voltageRegulator);

        // Port 1 Voltage regulator
        VoltagePort voltagePort1 = new VoltagePort(gridX + (4 * cellSize) + centerOffset, gridY + (4 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort1.update();
        voltagePorts.add(voltagePort1);

        // Port 2 Voltage regulator
        VoltagePort voltagePort2 = new VoltagePort(gridX + (5 * cellSize) + centerOffset, gridY + (1 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort2.update();
        voltagePorts.add(voltagePort2);

        // Port 3 Voltage regulator
        VoltagePort voltagePort3 = new VoltagePort(gridX + (4 * cellSize) + centerOffset, gridY + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort3.update();
        voltagePorts.add(voltagePort3);

        // Port 4 Voltage regulator
        VoltagePort voltagePort4 = new VoltagePort(gridX + (3 * cellSize) + centerOffset, gridY + (3 * cellSize) + centerOffset, cellSize * 0.5f, PortColor.WHITE);
        voltagePort4.update();
        voltagePorts.add(voltagePort4);

        // SWITCH
        Switch switch1 = new Switch(gridX + (2 * cellSize), gridY + (4 * cellSize), cellSize, PortColor.BLACK);
        switch1.hasPower = true; // Switches are connected to power
        switch1.update();
        switches.add(switch1);
    }

    public void initializeLevel(float gridX, float gridY, float gridWidth) {
        if (!initialized) {
            float cellSize = gridWidth / GameConstants.GRID_SIZE;

            switch (currentLevel) {
                case 1:
                    generateLevelOne(gridX, gridY, cellSize);
                    break;
                case 2:
                    generateLevelTwo(gridX, gridY, cellSize);
                    break;
                case 3:
                    generateLevelThree(gridX, gridY, cellSize);
                    break;
                case 4:
                    generateLevelFour(gridX, gridY, cellSize);
                    break;
                case 5:
                    generateLevelFive(gridX, gridY, cellSize);
                    break;
                case 6:
                    generateLevelSix(gridX, gridY, cellSize);
                    break;
                default:
                    generateLevelOne(gridX, gridY, cellSize); // Default
            }
            initialized = true;
        }
    }

    public void resetLevel() {
        initialized = false;
        clearElements();
    }

    public void updateElements() {
        for (Bulb bulb : bulbs) {
            bulb.update();
        }
        for (Switch sw : switches) {
            sw.update();
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
        for (Switch sw : switches) {
            sw.draw();
        }
        for (VoltageRegulator regulator : regulators) {
            regulator.draw();
        }
        for (VoltagePort vPort : voltagePorts) {
            vPort.draw();
        }
    }

    public void clearElements() {
        bulbs.clear();
        ports.clear();
        switches.clear();
        extensionCords.clear();
        plugs.clear();
        regulators.clear();
        voltagePorts.clear();
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

    public ArrayList<Switch> getSwitches() {
        return switches;
    }

    public ArrayList<WirePort> getPorts() {
        return ports;
    }

    public Switch getSwitchKey() {
        // If you only have one switch, return it
        for (Switch sw : getSwitches()) {
            if (sw.color == PortColor.BLACK) { // or some identifier for the key switch
                return sw;
            }
        }
        return null;
    }

    public int getTimeLimitForLevel(int level) {
        switch (level) {
            case 1: return 60; // 2 minutes
            case 2: return 90;
            case 3: return 90;
            case 4: return 120;
            case 5: return 150;
            case 6: return 180;
            default: return 120;
        }
    }

}