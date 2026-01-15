package nl.saxion.game.circuitchaos.core;

import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.PortColor;
import java.util.ArrayList;

public class HintManager {
    private boolean hintUsed = false;

    public HintManager() {
        this.hintUsed = false;
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

        boolean success = false;

        switch (level) {
            case 1:
                success = connectLevelOneHint(levelManager, connectionManager, gridX, gridY, cellSize);
                break;
            case 2:
                success = connectLevelTwoHint(levelManager, connectionManager, gridX, gridY, cellSize);
                break;
            case 3:
                success = connectLevelThreeHint(levelManager, connectionManager, gridX, gridY, cellSize);
                break;
            case 4:
                success = connectLevelFourHint(levelManager, connectionManager, gridX, gridY, cellSize);
                break;
            case 5:
                success = connectLevelFiveHint(levelManager, connectionManager, gridX, gridY, cellSize);
                break;
            case 6:
                success = connectLevelSixHint(levelManager, connectionManager, gridX, gridY, cellSize);
            default:
                System.out.println("No hint available for this level");
                return false;
        }

        if (success) {
            hintUsed = true;
            System.out.println("Hint used! Connection created automatically.");
        }

        return success;
    }

    private boolean connectLevelOneHint(LevelManager levelManager, TileConnectionManager connectionManager,
                                        float gridX, float gridY, float cellSize) {
        ArrayList<WirePort> ports = levelManager.getPorts();

        // Find RED ports
        WirePort redPort1 = null; // (0, 2)
        WirePort redPort2 = null; // (2, 0)

        for (WirePort port : ports) {
            if (port.color == PortColor.RED) {
                // Check position to identify which red port
                int gridPosX = Math.round((port.positionX - gridX) / cellSize);
                int gridPosY = Math.round((port.positionY - gridY) / cellSize);

                if (gridPosX == 0 && gridPosY == 2) {
                    redPort1 = port;
                } else if (gridPosX == 2 && gridPosY == 0) {
                    redPort2 = port;
                }
            }
        }

        if (redPort1 != null && redPort2 != null) {
            // Start building from first port
            connectionManager.startBuilding(redPort1, gridX, gridY, cellSize);

            // Add path: (0,2) -> (1,2) -> (2,2) -> (2,1) -> (2,0)
            connectionManager.addTileToPath(1, 2);
            connectionManager.addTileToPath(2, 2);
            connectionManager.addTileToPath(2, 1);

            // Finish at second port
            connectionManager.finishBuilding(redPort2);

            return true;
        }

        return false;
    }

    private boolean connectLevelTwoHint(LevelManager levelManager, TileConnectionManager connectionManager,
                                        float gridX, float gridY, float cellSize) {
        ArrayList<WirePort> ports = levelManager.getPorts();

        WirePort bluePort1 = null; // (0, 5)
        WirePort bluePort2 = null; // (2, 2)

        for (WirePort port : ports) {
            if (port.color == PortColor.BLUE) {
                int gridPosX = Math.round((port.positionX - gridX) / cellSize);
                int gridPosY = Math.round((port.positionY - gridY) / cellSize);

                if (gridPosX == 0 && gridPosY == 5) {
                    bluePort1 = port;
                } else if (gridPosX == 2 && gridPosY == 2) {
                    bluePort2 = port;
                }
            }
        }

        if (bluePort1 != null && bluePort2 != null) {
            connectionManager.startBuilding(bluePort1, gridX, gridY, cellSize);

            // Path: (0,5) -> (1,5) -> (2,5) -> (2,4) -> (2,3) -> (2,2)
            connectionManager.addTileToPath(0, 4);
            connectionManager.addTileToPath(0, 3);
            connectionManager.addTileToPath(0, 2);
            connectionManager.addTileToPath(1, 2);

            connectionManager.finishBuilding(bluePort2);
            return true;
        }

        return false;
    }

    private boolean connectLevelThreeHint(LevelManager levelManager, TileConnectionManager connectionManager,
                                          float gridX, float gridY, float cellSize) {
        ArrayList<WirePort> ports = levelManager.getPorts();

        WirePort greenPort1 = null; // (0, 5)
        WirePort greenPort2 = null; // (3, 4)

        for (WirePort port : ports) {
            if (port.color == PortColor.GREEN) {
                int gridPosX = Math.round((port.positionX - gridX) / cellSize);
                int gridPosY = Math.round((port.positionY - gridY) / cellSize);

                if (gridPosX == 0 && gridPosY == 5) {
                    greenPort1 = port;
                } else if (gridPosX == 3 && gridPosY == 4) {
                    greenPort2 = port;
                }
            }
        }

        if (greenPort1 != null && greenPort2 != null) {
            connectionManager.startBuilding(greenPort1, gridX, gridY, cellSize);

            // Path: (0,5) -> (1,5) -> (2,5) -> (3,5) -> (3,4)
            connectionManager.addTileToPath(1, 5);
            connectionManager.addTileToPath(2, 5);
            connectionManager.addTileToPath(3, 5);

            connectionManager.finishBuilding(greenPort2);
            return true;
        }

        return false;
    }

    private boolean connectLevelFourHint(LevelManager levelManager, TileConnectionManager connectionManager,
                                         float gridX, float gridY, float cellSize) {
        ArrayList<WirePort> ports = levelManager.getPorts();

        WirePort greenPort1 = null; // (0, 5)
        WirePort greenPort2 = null; // (4, 4)

        for (WirePort port : ports) {
            if (port.color == PortColor.GREEN) {
                int gridPosX = Math.round((port.positionX - gridX) / cellSize);
                int gridPosY = Math.round((port.positionY - gridY) / cellSize);

                if (gridPosX == 0 && gridPosY == 5) {
                    greenPort1 = port;
                } else if (gridPosX == 4 && gridPosY == 4) {
                    greenPort2 = port;
                }
            }
        }

        if (greenPort1 != null && greenPort2 != null) {
            connectionManager.startBuilding(greenPort1, gridX, gridY, cellSize);

            // Path: (0,5) -> (1,5) -> (2,5) -> (3,5) -> (4,5) -> (4,4)
            connectionManager.addTileToPath(1, 5);
            connectionManager.addTileToPath(2, 5);
            connectionManager.addTileToPath(3, 5);
            connectionManager.addTileToPath(4, 5);

            connectionManager.finishBuilding(greenPort2);
            return true;
        }

        return false;
    }

    private boolean connectLevelFiveHint(LevelManager levelManager, TileConnectionManager connectionManager,
                                         float gridX, float gridY, float cellSize) {
        ArrayList<WirePort> ports = levelManager.getPorts();

        WirePort redPort1 = null; // (0, 5)
        WirePort redPort2 = null; // (1, 3)

        for (WirePort port : ports) {
            if (port.color == PortColor.RED) {
                int gridPosX = Math.round((port.positionX - gridX) / cellSize);
                int gridPosY = Math.round((port.positionY - gridY) / cellSize);

                if (gridPosX == 0 && gridPosY == 5) {
                    redPort1 = port;
                } else if (gridPosX == 1 && gridPosY == 3) {
                    redPort2 = port;
                }
            }
        }

        if (redPort1 != null && redPort2 != null) {
            connectionManager.startBuilding(redPort1, gridX, gridY, cellSize);

            // Path: (0,5) -> (0,4) -> (0,3) -> (1,3)
            connectionManager.addTileToPath(0, 4);
            connectionManager.addTileToPath(0, 3);

            connectionManager.finishBuilding(redPort2);
            return true;
        }

        return false;
    }

    private boolean connectLevelSixHint(LevelManager levelManager,
                                        TileConnectionManager connectionManager,
                                        float gridX, float gridY, float cellSize) {

        ArrayList<Bulb> bulbs = levelManager.getBulbs();

        Bulb bulb1 = null; // (5,5)
        Bulb bulb2 = null; // (3,4)

        for (Bulb bulb : bulbs) {
            if (bulb.color == PortColor.YELLOW) {
                int gridPosX = Math.round((bulb.positionX - gridX) / cellSize);
                int gridPosY = Math.round((bulb.positionY - gridY) / cellSize);

                if (gridPosX == 5 && gridPosY == 5) {
                    bulb1 = bulb;
                } else if (gridPosX == 3 && gridPosY == 4) {
                    bulb2 = bulb;
                }
            }
        }

        if (bulb1 != null && bulb2 != null) {
            connectionManager.startBuilding(bulb1, gridX, gridY, cellSize);

            // Path: (5,5) → (4,5) → (3,5) → (3,4)
            connectionManager.addTileToPath(4, 5);
            connectionManager.addTileToPath(3, 5);

            connectionManager.finishBuilding(bulb2);
            return true;
        }

        return false;
    }

}