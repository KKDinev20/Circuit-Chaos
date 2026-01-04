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
    private int extensionCordsConnected = 0;
    private int plugsConnected = 0;

    // Required connections per level
    private int redRequired = 0;
    private int blueRequired = 0;
    private int greenRequired = 0;
    private int yellowRequired = 0;
    private int extensionCordsRequired = 0;
    private int plugsRequired = 0;

    private int heartsLost = 0;
    private boolean levelComplete = false;
    private float timeLeft = 120f;   // 2 minutes


    public void setupLevelOneConditions() {
        // Level 1: Need to connect 2 YELLOW bulbs, RED & BLUE ports
        yellowRequired = 1;
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 0;
        extensionCordsRequired = 0;
        plugsRequired = 0;
    }

    public void setupLevelTwoConditions() {
        // Level 1: Need to connect 2 YELLOW bulbs, RED & BLUE ports
        yellowRequired = 0;
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 1;
        extensionCordsRequired = 1;
        plugsRequired = 2;
    }

    public void checkConnections(
            TileConnectionManager connectionManager,
            ArrayList<WirePort> ports,
            ArrayList<Bulb> bulbs,
            ArrayList<ExtensionCord> extensionCords,
            ArrayList<PowerPlug> plugs) {

        // RESET EVERYTHING
        redConnected = 0;
        blueConnected = 0;
        greenConnected = 0;
        yellowConnected = 0;
        extensionCordsConnected = 0;
        plugsConnected = 0;

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

        // PLUGS
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
                extensionCordsConnected >= extensionCordsRequired &&
                plugsConnected >= plugsRequired) {

            levelComplete = true;
            return true;
        }

        return false;
    }

    public String getConnectionStatus() {
        String status = "";

        if (redRequired > 0) {
            status += "RED: " + redConnected + "/" + redRequired + "  ";
        }
        if (blueRequired > 0) {
            status += "BLUE: " + blueConnected + "/" + blueRequired + "  ";
        }
        if (greenRequired > 0) {
            status += "GREEN: " + greenConnected + "/" + greenRequired + "  ";
        }
        if (yellowRequired > 0) {
            status += "YELLOW: " + yellowConnected + "/" + yellowRequired;
        }
        if (extensionCordsRequired > 0) {
            status += "CORDS: " + extensionCordsConnected + "/" + extensionCordsRequired;
        }
        if (plugsRequired > 0) {
            status += "PLUGS: " + plugsConnected + "/" + plugsRequired;
        }

        return status;
    }

    public int getYellowConnected() { return yellowConnected; }
    public int getRedConnected() { return redConnected; }
    public int getBlueConnected() { return blueConnected; }
    public int getGreenConnected() { return greenConnected; }
    public int getExtensionCordsConnected() { return extensionCordsConnected; }
    public int getPlugsConnected() { return plugsConnected; }

    public int getYellowRequired() { return yellowRequired; }
    public int getRedRequired() { return redRequired; }
    public int getBlueRequired() { return blueRequired; }
    public int getGreenRequired() { return greenRequired; }
    public int getExtensionCordsRequired() { return extensionCordsRequired; }
    public int getPlugsRequired() { return plugsRequired; }

    public int getHeartsLost() {
        return heartsLost;
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public void reset() {
        heartsLost = 0;
        levelComplete = false;
        timeLeft = 120f;

        redConnected = 0;
        blueConnected = 0;
        greenConnected = 0;
        yellowConnected = 0;

        redRequired = 0;
        blueRequired = 0;
        greenRequired = 0;
        yellowRequired = 0;
    }

    public void checkConnections(TileConnectionManager connectionManager, ArrayList<WirePort> ports, ArrayList<Bulb> bulbs) {
    }
}