package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.Bulb;
import nl.saxion.game.circuitchaos.entities.WirePort;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;

import java.util.ArrayList;

public class WinConditionManager {
    // Simple counters for each color
    private int yellowConnected = 0;
    private int redConnected = 0;
    private int blueConnected = 0;
    private int greenConnected = 0;

    // Required connections per level
    private int redRequired = 0;
    private int blueRequired = 0;
    private int greenRequired = 0;
    private int yellowRequired = 0;

    private int heartsLost = 0;
    private boolean levelComplete = false;
    private float timeLeft = 120f;   // 2 minutes


    public void setupLevelOneConditions() {
        // Level 1: Need to connect 2 YELLOW bulbs, RED & BLUE ports
        yellowRequired = 1;
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 0;
    }

    public void setupLevelTwoConditions() {
        // Level 1: Need to connect 2 YELLOW bulbs, RED & BLUE ports
        yellowRequired = 0;
        redRequired = 1;
        blueRequired = 1;
        greenRequired = 1;
    }

    public void checkConnections(TileConnectionManager connectionManager,
                                 ArrayList<WirePort> ports,
                                 ArrayList<Bulb> bulbs) {
        // Reset counters
        redConnected = 0;
        blueConnected = 0;
        greenConnected = 0;
        yellowConnected = 0;

        // Check PORT pairs
        for (int i = 0; i < ports.size(); i++) {
            WirePort port1 = ports.get(i);

            for (int j = i + 1; j < ports.size(); j++) {
                WirePort port2 = ports.get(j);

                if (port1.color == port2.color &&
                        connectionManager.areElementsConnected(port1, port2)) {

                    if (port1.color == PortColor.RED) redConnected++;
                    else if (port1.color == PortColor.BLUE) blueConnected++;
                    else if (port1.color == PortColor.GREEN) greenConnected++;
                    else if (port1.color == PortColor.YELLOW) yellowConnected++;
                }
            }
        }

        // Check BULB pairs
        for (int i = 0; i < bulbs.size(); i++) {
            Bulb bulb1 = bulbs.get(i);

            for (int j = i + 1; j < bulbs.size(); j++) {
                Bulb bulb2 = bulbs.get(j);

                if (connectionManager.areElementsConnected(bulb1, bulb2)) {

                    if (bulb1.color == PortColor.YELLOW) yellowConnected++;
                }
            }
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
                yellowConnected >= yellowRequired) {

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

        return status;
    }

    public int getYellowConnected() { return yellowConnected; }
    public int getRedConnected() { return redConnected; }
    public int getBlueConnected() { return blueConnected; }
    public int getGreenConnected() { return greenConnected; }

    public int getYellowRequired() { return yellowRequired; }
    public int getRedRequired() { return redRequired; }
    public int getBlueRequired() { return blueRequired; }
    public int getGreenRequired() { return greenRequired; }

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
}