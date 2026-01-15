package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;

import java.util.ArrayList;

public class WinConditionManager {
    // Simple counters for each color
    private int yellowConnected = 0;
    private int redConnected = 0;
    private int blueConnected = 0;
    private int greenConnected = 0;
    private int orangeConnected = 0;
    private int pinkConnected = 0;
    private int purpleConnected = 0;
    private int extensionCordsConnected = 0;
    private int plugsConnected = 0;
    private int regulatorsConnected = 0;
    private int whiteConnected = 0;

    // Required connections per level
    private int redRequired = 0;
    private int blueRequired = 0;
    private int greenRequired = 0;
    private int yellowRequired = 0;
    private int orangeRequired = 0;
    private int pinkRequired = 0;
    private int purpleRequired = 0;
    private int extensionCordsRequired = 0;
    private int plugsRequired = 0;
    private int regulatorsRequired = 0;
    private int whiteRequired = 0;

    // Black ports and switch key
    private Switch switchKey; // the switch that must be ON
    private int blackPortsConnectedToSwitch = 0;
    private int blackPortsRequiredToSwitch = 0;

    private int heartsLost = 0;
    private boolean levelComplete = false;
    private float timeLeft = 120f;   // 2 minutes

    public void setupLevelOneConditions() {
        yellowRequired = 1;
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 0;
        extensionCordsRequired = 0;
        plugsRequired = 0;
        switchKey = null;
        blackPortsRequiredToSwitch = 0;
    }

    public void setupLevelTwoConditions() {
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 1;
        extensionCordsRequired = 1;
        plugsRequired = 2;
        switchKey = null;
        blackPortsRequiredToSwitch = 0;
    }

    public void setupLevelThreeConditions(Switch keySwitch) {
        // Clear previous level requirements
        reset();

        // Set Level 3 requirements
        redRequired = 1;
        greenRequired = 1;
        orangeRequired = 1;
        blackPortsRequiredToSwitch = 3;
        switchKey = keySwitch;
    }

    public void setupLevelFourConditions() {
        greenRequired = 1;
        purpleRequired = 1;
        pinkRequired = 1;
        redRequired = 1;
        extensionCordsRequired = 1;
        plugsRequired = 2;
        yellowRequired = 1;
    }

    public void setupLevelFiveConditions() {
        greenRequired = 1;
        blueRequired = 1;
        redRequired = 1;
        regulatorsRequired = 1;
        whiteRequired = 4;
    }

    public void setupLevelSixConditions(Switch keySwitch){
        redRequired = 1;
        yellowRequired = 1;
        regulatorsRequired = 1;
        whiteRequired = 4;
        blackPortsRequiredToSwitch = 3;
        switchKey = keySwitch;
        extensionCordsRequired = 1;
        plugsRequired = 2;

    }

    public void checkConnections(
            TileConnectionManager connectionManager,
            ArrayList<WirePort> ports,
            ArrayList<Bulb> bulbs,
            ArrayList<ExtensionCord> extensionCords,
            ArrayList<PowerPlug> plugs,
            ArrayList<VoltageRegulator> regulators,
            ArrayList<VoltagePort> voltagePorts,
            ArrayList<Tool> placedTools) {

        // RESET EVERYTHING
        redConnected = 0;
        blueConnected = 0;
        greenConnected = 0;
        yellowConnected = 0;
        orangeConnected = 0;
        pinkConnected = 0;
        purpleConnected = 0;
        extensionCordsConnected = 0;
        plugsConnected = 0;
        blackPortsConnectedToSwitch = 0;
        regulatorsConnected = 0;
        whiteConnected = 0;

        // PORT PAIRS
        for (int i = 0; i < ports.size(); i++) {
            for (int j = i + 1; j < ports.size(); j++) {
                WirePort a = ports.get(i);
                WirePort b = ports.get(j);

                if (a.color == b.color && connectionManager.areElementsConnected(a, b)) {
                    switch (a.color) {
                        case RED -> redConnected++;
                        case BLUE -> blueConnected++;
                        case GREEN -> greenConnected++;
                        case YELLOW -> yellowConnected++;
                        case ORANGE -> orangeConnected++;
                        case PINK ->  pinkConnected++;
                        case PURPLE -> purpleConnected++;
                    }
                }
            }
        }

        // BULBS
        for (int i = 0; i < bulbs.size(); i++) {
            for (int j = i + 1; j < bulbs.size(); j++) {
                if (connectionManager.areElementsConnected(bulbs.get(i), bulbs.get(j))) {
                    yellowConnected++;
                }
            }
        }

        // BLACK PORTS powered through tools
        blackPortsConnectedToSwitch = 0;
        System.out.println("=== DEBUG: Checking Switch Connections ===");
        System.out.println("Switch exists: " + (switchKey != null));
        if (switchKey != null) {
            System.out.println("Switch is ON: " + switchKey.isOn());
            System.out.println("Number of placed tools: " + placedTools.size());
        }

        if (switchKey != null && switchKey.isOn()) {
            // Check WirePorts for black connections
            for (WirePort port : ports) {
                if (port.color == PortColor.BLACK) {
                    boolean connected = connectionManager.areElementsConnected(port, switchKey);
                    System.out.println("WirePort (BLACK) connected to switch: " + connected);
                    if (connected) {
                        blackPortsConnectedToSwitch++;
                    }
                }
            }

            // Check placed tools (they are the black ports!)
            for (Tool tool : placedTools) {
                boolean connected = connectionManager.areElementsConnected(tool, switchKey);
                System.out.println("Tool at (" + tool.gridX + ", " + tool.gridY + ") connected to switch: " + connected);
                if (connected) {
                    blackPortsConnectedToSwitch++;
                }
            }
        }

        System.out.println("Black ports connected: " + blackPortsConnectedToSwitch + " / " + blackPortsRequiredToSwitch);

        // PLUGS connected to extension cords
        for (PowerPlug plug : plugs) {
            for (ExtensionCord cord : extensionCords) {
                if (connectionManager.areElementsConnected(plug, cord)) {
                    plugsConnected++;
                    break;
                }
            }
        }

        // EXTENSION CORDS (needs 2 plugs)
        for (ExtensionCord cord : extensionCords) {
            int connected = 0;
            for (PowerPlug plug : plugs) {
                if (connectionManager.areElementsConnected(cord, plug)) {
                    connected++;
                }
            }
            if (connected >= 2) extensionCordsConnected++;
        }

        for (VoltagePort vPort : voltagePorts) {
            for (VoltageRegulator regulator : regulators) {
                if (connectionManager.areElementsConnected(vPort, regulator)) {
                    whiteConnected++;
                    break;
                }
            }
        }

        for (VoltageRegulator regulator : regulators) {
            int connected = 0;
            for (VoltagePort vPort : voltagePorts) {
                if (connectionManager.areElementsConnected(regulator, vPort)) {
                    connected++;
                }
            }
            if (connected >= 4) regulatorsConnected++;
        }

        // DEBUG: Print final counts
        System.out.println("=== Connection Summary ===");
        System.out.println("Red: " + redConnected + "/" + redRequired);
        System.out.println("Green: " + greenConnected + "/" + greenRequired);
        System.out.println("Orange: " + orangeConnected + "/" + orangeRequired);
        System.out.println("Yellow: " + yellowConnected + "/" + yellowRequired);
        System.out.println("Blue: " + blueConnected + "/" + blueRequired);
        System.out.println("Black (to switch): " + blackPortsConnectedToSwitch + "/" + blackPortsRequiredToSwitch);
        System.out.println("Regulators: " + regulatorsConnected + "/" + regulatorsRequired);
        System.out.println("White: " + whiteConnected + "/" + whiteRequired);
    }

    public int calculateHeartsLost() {
        int totalRequired = redRequired + blueRequired + greenRequired + yellowRequired;
        int totalConnected = redConnected + blueConnected + greenConnected + yellowConnected;

        heartsLost = totalRequired - totalConnected;
        return heartsLost;
    }

    public boolean checkWinCondition() {
        if (redConnected >= redRequired &&
                blueConnected >= blueRequired &&
                greenConnected >= greenRequired &&
                yellowConnected >= yellowRequired &&
                orangeConnected >= orangeRequired &&
                extensionCordsConnected >= extensionCordsRequired &&
                plugsConnected >= plugsRequired &&
                blackPortsConnectedToSwitch >= blackPortsRequiredToSwitch &&
                regulatorsConnected >= regulatorsRequired &&
                whiteConnected >= whiteRequired &&
                pinkConnected>= pinkRequired &&
                purpleConnected>= pinkRequired &&
                (switchKey == null || switchKey.isOn())) {

            levelComplete = true;
            return true;
        }

        return false;
    }

    // --- Getters ---
    public int getYellowConnected() { return yellowConnected; }
    public int getRedConnected() { return redConnected; }
    public int getBlueConnected() { return blueConnected; }
    public int getGreenConnected() { return greenConnected; }
    public int getOrangeConnected() { return orangeConnected; }
    public int getExtensionCordsConnected() { return extensionCordsConnected; }
    public int getPlugsConnected() { return plugsConnected; }
    public int getRegulatorsConnected() { return regulatorsConnected; }
    public int getWhiteConnected() { return whiteConnected; }
    public int getPinkConnected() { return pinkConnected; }
    public int getPinkRequired() { return pinkRequired; }

    public int getPurpleConnected() { return purpleConnected; }
    public int getPurpleRequired() { return purpleRequired; }


    public int getYellowRequired() { return yellowRequired; }
    public int getRedRequired() { return redRequired; }
    public int getBlueRequired() { return blueRequired; }
    public int getOrangeRequired() { return orangeRequired; }
    public int getGreenRequired() { return greenRequired; }
    public int getExtensionCordsRequired() { return extensionCordsRequired; }
    public int getPlugsRequired() { return plugsRequired; }
    public int getRegulatorsRequired() { return regulatorsRequired; }
    public int getWhiteRequired() { return whiteRequired; }

    public int getBlackPortsConnectedToSwitch() { return blackPortsConnectedToSwitch; }
    public int getBlackPortsRequiredToSwitch() { return blackPortsRequiredToSwitch; }

    public int getHeartsLost() { return heartsLost; }
    public boolean isLevelComplete() { return levelComplete; }

    public void reset() {
        heartsLost = 0;
        levelComplete = false;
        timeLeft = 120f;

        redConnected = 0;
        blueConnected = 0;
        greenConnected = 0;
        yellowConnected = 0;
        orangeConnected = 0;
        pinkConnected = 0;
        purpleConnected = 0;
        extensionCordsConnected = 0;
        plugsConnected = 0;
        regulatorsConnected = 0;
        whiteConnected = 0;

        redRequired = 0;
        blueRequired = 0;
        orangeRequired = 0;
        greenRequired = 0;
        yellowRequired = 0;
        pinkRequired = 0;
        purpleRequired = 0;
        extensionCordsRequired = 0;
        plugsRequired = 0;
        regulatorsRequired = 0;
        whiteRequired = 0;

        switchKey = null;
        blackPortsConnectedToSwitch = 0;
        blackPortsRequiredToSwitch = 0;
    }


}
