package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.core.*;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.util.GameConstants;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

public class YourGameScreen extends ScalableGameScreen {
    private Box centeredBox = new Box();
    private ToolManager toolManager;
    private LevelManager levelManager;
    private TileConnectionManager connectionManager;
    private Tool currentlyDragging = null;
    private boolean showQuitMenu = false;
    private WinConditionManager winManager;
    private boolean levelEnded = false;

    // TIMER
    private float timeLeft = 35f;   // 2 minutes

    private boolean showEndScreen = false;
    private boolean wonLevel = false;


    // UI positions for quit menu
    private float btnYesX, btnYesY, btnYesW, btnYesH;
    private float btnNoX, btnNoY, btnNoW, btnNoH;
    private float panelX, panelY, panelW, panelH;
    private boolean yesHover = false, noHover = false;

    private float btnContinueX, btnContinueY, btnContinueW, btnContinueH;
    private float btnRetryX, btnRetryY, btnRetryW, btnRetryH;
    private float btnMenuX, btnMenuY, btnMenuW, btnMenuH;
    private boolean continueHover = false, retryHover = false, menuHover = false;


    public YourGameScreen() {
        super(1280, 720);
        toolManager = new ToolManager();
        levelManager = new LevelManager();
        connectionManager = new TileConnectionManager();
        winManager = new WinConditionManager();
    }

    @Override
    public void show() {
        GameApp.addFont("pixel_timer", "fonts/PressStart2P-Regular.ttf", 20);
        GameApp.addFont("levelSelectFont", "fonts/Cause-Medium.ttf", 40);

        // RESET ALL STATE
        levelManager.resetLevel();
        connectionManager.reset();
        toolManager = new ToolManager();
        winManager.reset();

        // Reset game state flags
        levelEnded = false;
        showEndScreen = false;
        wonLevel = false;
        showQuitMenu = false;
        timeLeft = 35f;
        currentlyDragging = null;

        enableHUD((int) getWorldWidth(), (int) getWorldHeight());
        ElementManager.addTextures();
        GameApp.addTexture("level1", "textures/backgrounds/house.png");

        centeredBox.width = GameConstants.GRID_WIDTH;
        centeredBox.height = GameConstants.GRID_HEIGHT;

        // Setup win conditions based on current level
        if (LevelManager.currentLevel == 1) {
            winManager.setupLevelOneConditions();
        } else if (LevelManager.currentLevel == 2) {
            winManager.setupLevelTwoConditions();
        } else if (LevelManager.currentLevel == 3) {
            winManager.setupLevelThreeConditions();
        } else {
            // Handle undefined levels - return to menu or show message
            System.out.println("Level " + LevelManager.currentLevel + " not yet implemented!");
            LevelManager.currentLevel = 1;
            GameApp.switchScreen("MainMenuScreen");
        }
        // Add more levels here as needed

        // Quit menu positions
        panelW = 600;
        panelH = 300;
        panelX = getWorldWidth() / 2f - panelW / 2f;
        panelY = getWorldHeight() / 2f - panelH / 2f;

        btnYesW = 200;
        btnYesH = 80;
        btnYesX = panelX + 60;
        btnYesY = panelY + 40;

        btnNoW = 200;
        btnNoH = 80;
        btnNoX = panelX + panelW - btnNoW - 60;
        btnNoY = panelY + 40;

        System.out.println("YourGameScreen.show() - Current level: " + LevelManager.currentLevel);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        float centerX = getWorldWidth() / 2;
        float centerY = getWorldHeight() / 2;
        float gridX = centerX - centeredBox.width / 2;
        float gridY = centerY - centeredBox.height / 2;

        // Handle ESC key for quit menu (only if not showing end screen)
        if (GameApp.isKeyJustPressed(Input.Keys.ESCAPE) && !showEndScreen) {
            showQuitMenu = !showQuitMenu;
        }

        // PRIORITY 1: SHOW END SCREEN (win or lose)
        if (showEndScreen) {
            renderEndScreen();
            renderEndScreenClick();
            return; // Stop here
        }

        // PRIORITY 2: SHOW QUIT MENU
        if (showQuitMenu) {
            renderQuitMenu();
            renderQuitMenuClick();
            return; // Stop here
        }

        // GAME IS RUNNING - update timer and check conditions
        if (!levelEnded) {
            timeLeft -= delta;

            // CHECK WIN CONDITION EVERY FRAME
            winManager.checkConnections(connectionManager, levelManager.getPorts(), levelManager.getBulbs(), levelManager.getExtensionCords(), levelManager.getPlugs());

            if (winManager.checkWinCondition()) {
                // LEVEL COMPLETE!
                levelEnded = true;
                wonLevel = true;
                showEndScreen = true;

                System.out.println("=== LEVEL COMPLETE! ===");
                System.out.println("Time remaining: " + timeLeft + " seconds");
                System.out.println("Hearts lost: " + winManager.calculateHeartsLost());
                return; // Stop this frame
            }

            // Check if time ran out (LOSE condition)
            if (timeLeft <= 0) {
                timeLeft = 0;
                levelEnded = true;
                wonLevel = false;
                showEndScreen = true;

                System.out.println("=== TIME UP! ===");
                return; // Stop this frame
            }

            // Start breaking wires when 30 seconds left
            if (timeLeft < 30f && timeLeft > 0) {
                connectionManager.updateWireBreaking(delta, true);
            }
        }

        // Initialize tools if not done
        if (toolManager.getToolboxTools()[0] == null) {
            toolManager.initializeTools(gridX, gridY);
        }

        // Initialize level if not done
        levelManager.initializeLevel(gridX, gridY, centeredBox.width);

        // Handle player input (only if level not ended)
        if (!levelEnded) {
            handleInput(gridX, gridY);
        }

        // Update level elements
        levelManager.updateElements();
        connectionManager.updateWirePaths();

        // === RENDERING ===
        GameApp.startSpriteRendering();
        GameApp.drawTexture("level1", 0, 0, getWorldWidth(), getWorldHeight());
        GridManager.drawGrid(gridX, gridY, centeredBox.width);
        connectionManager.drawWirePathsTextures();
        levelManager.drawElements();
        drawHearts(gridX, gridY);
        drawTimer(gridX, gridY);
        drawHintsButton(gridX, gridY);
        GameApp.endSpriteRendering();

        GameApp.startShapeRenderingFilled();
        drawToolBoxes(gridX, gridY);
        connectionManager.drawWirePathsPreview();
        toolManager.drawTools();
        GameApp.endShapeRendering();
    }

    private void handleInput(float gridX, float gridY) {
        float mouseX = getMouseX();
        float mouseY = getMouseY();
        float cellSize = centeredBox.width / GameConstants.GRID_SIZE;

        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {

            // 1. Check if clicked on circuit element
            boolean clickedElement = false;

            // Check bulbs
            for (Bulb bulb : levelManager.getBulbs()) {
                if (bulb.contains(mouseX, mouseY)) {
                    if (connectionManager.isBuilding()) {
                        connectionManager.finishBuilding(bulb);
                    } else {
                        connectionManager.startBuilding(bulb, gridX, gridY, cellSize);
                    }
                    clickedElement = true;
                    break;
                }
            }

            // Check ports
            if (!clickedElement) {
                for (WirePort port : levelManager.getPorts()) {
                    if (port.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            // Finish connection - NO GRID PARAMETERS
                            connectionManager.finishBuilding(port);
                        } else {
                            // Start new connection - ADD GRID PARAMETERS
                            connectionManager.startBuilding(port, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check extension cords
            if (!clickedElement) {
                for (ExtensionCord cord : levelManager.getExtensionCords()) {
                    if (cord.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            // Finish connection - NO GRID PARAMETERS
                            connectionManager.finishBuilding(cord);
                        } else {
                            // Start new connection - ADD GRID PARAMETERS
                            connectionManager.startBuilding(cord, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check plugs
            if (!clickedElement) {
                for (PowerPlug plug : levelManager.getPlugs()) {
                    if (plug.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(plug);
                        } else {
                            connectionManager.startBuilding(plug, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // 2. If clicked on grid tile (and we're building)
            if (!clickedElement && connectionManager.isBuilding() &&
                    mouseX > gridX && mouseX < gridX + centeredBox.width &&
                    mouseY > gridY && mouseY < gridY + centeredBox.height) {

                // Get grid coordinates
                int gridCellX = (int) ((mouseX - gridX) / cellSize);
                int gridCellY = (int) ((mouseY - gridY) / cellSize);

                // Get tile center
                float tileCenterX = gridX + gridCellX * cellSize + cellSize / 2;
                float tileCenterY = gridY + gridCellY * cellSize + cellSize / 2;

                // Add to path (checks adjacency automatically)
                boolean added = connectionManager.addTileToPath(gridCellX, gridCellY);
                if (!added) {
                    System.out.println("Tile not adjacent to path!");
                }
            }

            // 3. If not wiring, check tools
            if (!clickedElement && !connectionManager.isBuilding()) {
                currentlyDragging = toolManager.getToolAtPosition(mouseX, mouseY);
            }
        }

        // Cancel with right click or ESC
        if (GameApp.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (connectionManager.isBuilding()) {
                connectionManager.cancelBuilding();
            } else {
                // Try to repair broken wire
                connectionManager.repairWireAt(mouseX, mouseY);
            }
        }

        if (GameApp.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (connectionManager.isBuilding()) {
                connectionManager.cancelBuilding();
            } else {
                connectionManager.repairWireAt(mouseX, mouseY);
            }
        }

        if (GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            currentlyDragging.x = mouseX - currentlyDragging.width / 2;
            currentlyDragging.y = mouseY - currentlyDragging.height / 2;
        }

        if (!GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            if (mouseX > gridX && mouseX < gridX + centeredBox.width &&
                    mouseY > gridY && mouseY < gridY + centeredBox.height) {

                // Calculate grid cell
                int gridCellX = (int) ((mouseX - gridX) / cellSize);
                int gridCellY = (int) ((mouseY - gridY) / cellSize);

                // Check if cell is occupied by level element
                if (levelManager.isCellOccupied(gridCellX, gridCellY, gridX, gridY, cellSize)) {
                    // Can't place tool here - cell has bulb/port
                    toolManager.returnToolToToolbox(currentlyDragging);
                } else {
                    // Try to place tool
                    boolean placementSuccessful = toolManager.placeTool(
                            currentlyDragging, gridX, gridY, mouseX, mouseY);
                    if (!placementSuccessful) {
                        toolManager.returnToolToToolbox(currentlyDragging);
                    }
                }
            } else {
                toolManager.returnToolToToolbox(currentlyDragging);
            }
            currentlyDragging = null;
        }
    }

    private void drawHearts(float gridX, float gridY) {
        float heartsX = gridX;
        float heartsY = gridY + GameConstants.GRID_HEARTS_SPACING + GameConstants.GRID_HEIGHT - 25;

        GameApp.addTextureAtlas("heart", "textures/atlases/hearts.atlas");
        for (int i = 0; i < GameConstants.HEARTS_COUNT; i++) {
            float heartX = heartsX + (i * (GameConstants.HEARTS_SIZE + GameConstants.HEARTS_SPACING));

            GameApp.drawAtlasRegion("heart", "hearts", heartX, heartsY, GameConstants.HEARTS_SIZE, GameConstants.HEARTS_SIZE);
        }
    }

    private void drawHintsButton(float gridX, float gridY) {
        float buttonWidth = 120f;
        float buttonHeight = 50f;
        float buttonX = gridX + centeredBox.width - buttonWidth;
        float buttonY = gridY + 10f + centeredBox.height;

        GameApp.addTexture("hint", "textures/hint.png");
        GameApp.drawTexture("hint", buttonX, buttonY, buttonWidth, buttonHeight);
    }

    private void drawTimer(float gridX, float gridY) {
        int totalSeconds = (int) timeLeft;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String timeText = String.format("%02d:%02d", minutes, seconds);

        float x = getWorldWidth() / 2f;
        float y = gridY + GameConstants.GRID_HEIGHT + GameConstants.GRID_HEARTS_SPACING + 60;

        float border = 4f; // thickness of border (pixel look)

        // color border
        GameApp.drawTextCentered("pixel_timer", timeText, x - border, y, "black");
        GameApp.drawTextCentered("pixel_timer", timeText, x + border, y, "black");
        GameApp.drawTextCentered("pixel_timer", timeText, x, y - border, "black");
        GameApp.drawTextCentered("pixel_timer", timeText, x, y + border, "black");

        //text color
        GameApp.drawTextCentered("pixel_timer", timeText, x, y, "orange-400");
    }

    private void drawToolBoxes(float gridX, float gridY) {
        float toolY = gridY - GameConstants.GRID_TOOL_SPACING - GameConstants.TOOL_SIZE;
        float startX = gridX + 15f;

        for (int i = 0; i < GameConstants.TOOL_COUNT; i++) {
            float toolX = startX + (i * GameConstants.TOOL_SPACING);
            GameApp.drawRect(toolX, toolY, GameConstants.TOOL_SIZE, GameConstants.TOOL_SIZE, Color.DARK_GRAY);
        }
    }

    /*private void drawTimer(float gridX, float gridY) {
        float timerHeight = 10f;
        float timerY = gridY + centeredBox.height + 8f;

        GameApp.drawRect(gridX, timerY, centeredBox.width, timerHeight, Color.GREEN);
        GameApp.drawRect(gridX, timerY, centeredBox.width - 80f, timerHeight, Color.RED);
    }*/

    private void renderQuitMenu() {
        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        // detect hover
        yesHover = (mx >= btnYesX && mx <= btnYesX + btnYesW && my >= btnYesY && my <= btnYesY + btnYesH);

        noHover = (mx >= btnNoX && mx <= btnNoX + btnNoW && my >= btnNoY && my <= btnNoY + btnNoH);

        // background of level stays
        GameApp.startSpriteRendering();
        GameApp.drawTexture("level1", 0, 0, getWorldWidth(), getWorldHeight());
        GameApp.endSpriteRendering();

        // ==== DRAW PANEL ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(panelX, panelY, panelW, panelH, "gray-800");
        GameApp.endShapeRendering();

        // ==== DRAW YES BUTTON ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(btnYesX - (yesHover ? 5 : 0), btnYesY - (yesHover ? 5 : 0), btnYesW + (yesHover ? 10 : 0), btnYesH + (yesHover ? 10 : 0), yesHover ? "blue-400" : "blue-300");
        GameApp.endShapeRendering();

        // ==== DRAW NO BUTTON ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(btnNoX - (noHover ? 5 : 0), btnNoY - (noHover ? 5 : 0), btnNoW + (noHover ? 10 : 0), btnNoH + (noHover ? 10 : 0), noHover ? "red-400" : "red-300");
        GameApp.endShapeRendering();

        // ==== TEXT ====
        GameApp.startSpriteRendering();

        GameApp.drawTextCentered("levelSelectFont", "Are you sure you", panelX + panelW / 2f, panelY + panelH - 60f, "white");

        GameApp.drawTextCentered("levelSelectFont", "want to quit?", panelX + panelW / 2f, panelY + panelH - 130f, "white");

        GameApp.drawTextCentered("buttonFont", "Yes", btnYesX + btnYesW / 2f, btnYesY + btnYesH / 2f, "white");

        GameApp.drawTextCentered("buttonFont", "No", btnNoX + btnNoW / 2f, btnNoY + btnNoH / 2f, "white");

        GameApp.endSpriteRendering();
    }

    private void renderQuitMenuClick() {
        if (!GameApp.isButtonJustPressed(Input.Buttons.LEFT)) return;

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        // YES
        if (mx >= btnYesX && mx <= btnYesX + btnYesW && my >= btnYesY && my <= btnYesY + btnYesH) {
            levelManager.resetLevel();
            connectionManager.reset();
            GameApp.switchScreen("MainMenuScreen");
            showQuitMenu = false;
            return;
        }

        // NO
        if (mx >= btnNoX && mx <= btnNoX + btnNoW && my >= btnNoY && my <= btnNoY + btnNoH) {
            showQuitMenu = false;
            return;
        }
    }

    private float[] windowToWorldMouse() {
        float wx = GameApp.getMousePositionInWindowX();
        float wy = GameApp.getMousePositionInWindowY();

        int winW = com.badlogic.gdx.Gdx.graphics.getWidth();
        int winH = com.badlogic.gdx.Gdx.graphics.getHeight();

        float worldW = getWorldWidth();
        float worldH = getWorldHeight();

        float sx = worldW / winW;
        float sy = worldH / winH;

        float worldX = wx * sx;
        float worldY = (winH - wy) * sy; // flip Y

        return new float[]{worldX, worldY};
    }

    private void renderEndScreen() {
        // Draw game in background
        float centerX = getWorldWidth() / 2;
        float centerY = getWorldHeight() / 2;
        float gridX = centerX - centeredBox.width / 2;
        float gridY = centerY - centeredBox.height / 2;

        GameApp.startSpriteRendering();
        GameApp.drawTexture("level1", 0, 0, getWorldWidth(), getWorldHeight());
        GridManager.drawGrid(gridX, gridY, centeredBox.width);
        levelManager.drawElements();
        connectionManager.drawWirePathsTextures();
        GameApp.endSpriteRendering();

        // Semi-transparent overlay
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(0, 0, getWorldWidth(), getWorldHeight(), new Color(0, 0, 0, 0.7f));
        GameApp.endShapeRendering();

        // Calculate positions
        panelW = 800;
        panelH = 550;
        panelX = getWorldWidth() / 2f - panelW / 2f;
        panelY = getWorldHeight() / 2f - panelH / 2f;

        float buttonWidth = 220;
        float buttonHeight = 70;
        float buttonSpacing = 40;

        // Get mouse position
        float mouseX = getMouseX(); // Use getMouseX() directly
        float mouseY = getMouseY();

        // Panel background
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(panelX, panelY, panelW, panelH, "gray-900");
        GameApp.endShapeRendering();

        int yellowConnected = winManager.getYellowConnected();
        int yellowRequired = winManager.getYellowRequired();
        int redConnected = winManager.getRedConnected();
        int redRequired = winManager.getRedRequired();
        int blueConnected = winManager.getBlueConnected();
        int blueRequired = winManager.getBlueRequired();
        int greenConnected = winManager.getGreenConnected();
        int greenRequired = winManager.getGreenRequired();
        int extensionCordsConnected = winManager.getExtensionCordsConnected();
        int extensionCordsRequired = winManager.getExtensionCordsRequired();
        int plugsConnected = winManager.getPlugsConnected();
        int plugsRequired = winManager.getPlugsRequired();

        if (wonLevel) {
            // === WIN SCREEN ===

            // Button positions
            btnContinueW = buttonWidth;
            btnContinueH = buttonHeight;
            btnContinueX = panelX + panelW / 2f - btnContinueW - buttonSpacing / 2f;
            btnContinueY = panelY + 50;

            btnMenuW = buttonWidth;
            btnMenuH = buttonHeight;
            btnMenuX = panelX + panelW / 2f + buttonSpacing / 2f;
            btnMenuY = panelY + 50;

            // Check hover
            continueHover = (mouseX >= btnContinueX && mouseX <= btnContinueX + btnContinueW &&
                    mouseY >= btnContinueY && mouseY <= btnContinueY + btnContinueH);
            menuHover = (mouseX >= btnMenuX && mouseX <= btnMenuX + btnMenuW &&
                    mouseY >= btnMenuY && mouseY <= btnMenuY + btnMenuH);

            // Draw buttons
            GameApp.startShapeRenderingFilled();
            GameApp.drawRect(btnContinueX - (continueHover ? 5 : 0),
                    btnContinueY - (continueHover ? 5 : 0),
                    btnContinueW + (continueHover ? 10 : 0),
                    btnContinueH + (continueHover ? 10 : 0),
                    continueHover ? "green-400" : "green-600");

            GameApp.drawRect(btnMenuX - (menuHover ? 5 : 0),
                    btnMenuY - (menuHover ? 5 : 0),
                    btnMenuW + (menuHover ? 10 : 0),
                    btnMenuH + (menuHover ? 10 : 0),
                    menuHover ? "blue-400" : "blue-600");
            GameApp.endShapeRendering();

            // Draw text content
            GameApp.startSpriteRendering();

            // Title
            GameApp.drawTextCentered("levelSelectFont", "LEVEL COMPLETE!",
                    panelX + panelW / 2f, panelY + panelH - 50f, "green-400");

            // Stats section
            float statsY = panelY + panelH - 140f;
            float lineHeight = 35f;

            // Time
            int totalSeconds = (int) timeLeft;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);
            GameApp.drawTextCentered("pixel_timer", timeText, panelX + panelW / 2f, statsY, "white");
            statsY -= lineHeight;

            // Hearts lost
            int heartsLost = winManager.calculateHeartsLost();
            String heartsText = "Hearts Lost: " + heartsLost + " / 3";
            GameApp.drawTextCentered("pixel_timer", heartsText, panelX + panelW / 2f, statsY,
                    heartsLost == 0 ? "green-300" : "orange-400");
            statsY -= lineHeight;

            // Connection details
            statsY -= 10f; // Extra space
            GameApp.drawText("pixel_timer", "Connections:", panelX + 80f, statsY, "yellow-400");
            statsY -= lineHeight;

            // Yellow bulbs
            if (yellowRequired > 0) {
                String status = yellowConnected >= yellowRequired ? "[✓]" : "[✗]";
                String color = yellowConnected >= yellowRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Yellow Bulbs: " + yellowConnected + "/" + yellowRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Red ports
            if (redRequired > 0) {
                String status = redConnected >= redRequired ? "[✓]" : "[✗]";
                String color = redConnected >= redRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Red Ports: " + redConnected + "/" + redRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Blue ports
            if (blueRequired > 0) {
                String status = blueConnected >= blueRequired ? "[✓]" : "[✗]";
                String color = blueConnected >= blueRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Blue Ports: " + blueConnected + "/" + blueRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Green ports
            if (greenRequired > 0) {
                String status = greenConnected >= greenRequired ? "[✓]" : "[✗]";
                String color = greenConnected >= greenRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Green Ports: " + greenConnected + "/" + greenRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            if (extensionCordsRequired > 0) {
                String status = extensionCordsConnected >= extensionCordsRequired ? "[✓]" : "[✗]";
                String color = extensionCordsConnected >= extensionCordsRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Extension Cords: " + extensionCordsConnected + "/" + extensionCordsRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            if (plugsConnected > 0) {
                String status = plugsConnected >= plugsRequired ? "[✓]" : "[✗]";
                String color = plugsConnected >= plugsRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Plugs: " + plugsConnected + "/" + plugsRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Button text
            GameApp.drawTextCentered("levelSelectFont", "Continue",
                    btnContinueX + btnContinueW / 2f, btnContinueY + btnContinueH / 2f, "white");
            GameApp.drawTextCentered("levelSelectFont", "Menu",
                    btnMenuX + btnMenuW / 2f, btnMenuY + btnMenuH / 2f, "white");

            GameApp.endSpriteRendering();

        } else {
            // === LOSE SCREEN ===

            // Button positions
            btnRetryW = buttonWidth;
            btnRetryH = buttonHeight;
            btnRetryX = panelX + panelW / 2f - btnRetryW - buttonSpacing / 2f;
            btnRetryY = panelY + 50;

            btnMenuW = buttonWidth;
            btnMenuH = buttonHeight;
            btnMenuX = panelX + panelW / 2f + buttonSpacing / 2f;
            btnMenuY = panelY + 50;

            // Check hover
            retryHover = (mouseX >= btnRetryX && mouseX <= btnRetryX + btnRetryW &&
                    mouseY >= btnRetryY && mouseY <= btnRetryY + btnRetryH);
            menuHover = (mouseX >= btnMenuX && mouseX <= btnMenuX + btnMenuW &&
                    mouseY >= btnMenuY && mouseY <= btnMenuY + btnMenuH);

            // Draw buttons
            GameApp.startShapeRenderingFilled();
            GameApp.drawRect(btnRetryX - (retryHover ? 5 : 0),
                    btnRetryY - (retryHover ? 5 : 0),
                    btnRetryW + (retryHover ? 10 : 0),
                    btnRetryH + (retryHover ? 10 : 0),
                    retryHover ? "yellow-400" : "yellow-600");

            GameApp.drawRect(btnMenuX - (menuHover ? 5 : 0),
                    btnMenuY - (menuHover ? 5 : 0),
                    btnMenuW + (menuHover ? 10 : 0),
                    btnMenuH + (menuHover ? 10 : 0),
                    menuHover ? "blue-400" : "blue-600");
            GameApp.endShapeRendering();

            // Draw text content
            GameApp.startSpriteRendering();

            // Title
            GameApp.drawTextCentered("levelSelectFont", "TIME'S UP!",
                    panelX + panelW / 2f, panelY + panelH - 50f, "red-400");

            // Stats section
            float statsY = panelY + panelH - 140f;
            float lineHeight = 35f;

            // Hearts lost
            int heartsLost = winManager.calculateHeartsLost();
            String heartsText = "Hearts Lost: " + heartsLost + " / 3";
            GameApp.drawTextCentered("pixel_timer", heartsText, panelX + panelW / 2f, statsY, "red-400");
            statsY -= lineHeight;

            // Connection details
            statsY -= 10f;
            GameApp.drawText("pixel_timer", "Missing Connections:", panelX + 80f, statsY, "red-400");
            statsY -= lineHeight;

            // Yellow bulbs
            if (yellowRequired > 0) {
                String status = yellowConnected >= yellowRequired ? "[✓]" : "[✗]";
                String color = yellowConnected >= yellowRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Yellow Bulbs: " + yellowConnected + "/" + yellowRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Red ports
            if (redRequired > 0) {
                String status = redConnected >= redRequired ? "[✓]" : "[✗]";
                String color = redConnected >= redRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Red Ports: " + redConnected + "/" + redRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Blue ports
            if (blueRequired > 0) {
                String status = blueConnected >= blueRequired ? "[✓]" : "[✗]";
                String color = blueConnected >= blueRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Blue Ports: " + blueConnected + "/" + blueRequired,
                        panelX + 100f, statsY, color);
                statsY -= lineHeight;
            }

            // Green ports
            if (greenRequired > 0) {
                String status = greenConnected >= greenRequired ? "[✓]" : "[✗]";
                String color = greenConnected >= greenRequired ? "green-300" : "red-400";
                GameApp.drawText("pixel_timer", status + " Green Ports: " + greenConnected + "/" + greenRequired,
                        panelX + 100f, statsY, color);
            }

            // Button text
            GameApp.drawTextCentered("levelSelectFont", "Retry",
                    btnRetryX + btnRetryW / 2f, btnRetryY + btnRetryH / 2f, "white");
            GameApp.drawTextCentered("levelSelectFont", "Menu",
                    btnMenuX + btnMenuW / 2f, btnMenuY + btnMenuH / 2f, "white");

            GameApp.endSpriteRendering();
        }
    }

    private void renderEndScreenClick() {
        if (!GameApp.isButtonJustPressed(Input.Buttons.LEFT)) return;

        float mouseX = getMouseX();
        float mouseY = getMouseY();

        if (wonLevel) {
            // Continue button
            if (mouseX >= btnContinueX && mouseX <= btnContinueX + btnContinueW &&
                    mouseY >= btnContinueY && mouseY <= btnContinueY + btnContinueH) {
                System.out.println("Continue clicked - moving to level " + (LevelManager.currentLevel + 1));
                LevelManager.currentLevel++;
                GameApp.switchScreen("YourGameScreen");
                return;
            }

            // Menu button (win)
            if (mouseX >= btnMenuX && mouseX <= btnMenuX + btnMenuW &&
                    mouseY >= btnMenuY && mouseY <= btnMenuY + btnMenuH) {
                System.out.println("Menu clicked from win screen");
                GameApp.switchScreen("MainMenuScreen");
                return;
            }
        } else {
            // Retry button
            if (mouseX >= btnRetryX && mouseX <= btnRetryX + btnRetryW &&
                    mouseY >= btnRetryY && mouseY <= btnRetryY + btnRetryH) {
                System.out.println("Retry clicked");
                GameApp.switchScreen("YourGameScreen");
                return;
            }

            // Menu button (lose)
            if (mouseX >= btnMenuX && mouseX <= btnMenuX + btnMenuW &&
                    mouseY >= btnMenuY && mouseY <= btnMenuY + btnMenuH) {
                System.out.println("Menu clicked from lose screen");
                GameApp.switchScreen("MainMenuScreen");
                return;
            }
        }
    }

    @Override
    public void hide() {
        // Cleanup if needed
        GameApp.disposeUIElements();
    }
}