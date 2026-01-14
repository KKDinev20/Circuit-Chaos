package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.core.*;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.entities.enums.*;
import nl.saxion.game.circuitchaos.ui.UIButton;
import nl.saxion.game.circuitchaos.util.GameConstants;
import nl.saxion.game.circuitchaos.util.HelperMethods;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

import java.awt.*;
import java.util.ArrayList;

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
    private float timeLeft = 65f;   // 2 minutes

    private boolean showEndScreen = false;
    private boolean wonLevel = false;


    // UI positions for quit menu
    private float btnYesX, btnYesY, btnYesW, btnYesH;
    private float btnNoX, btnNoY, btnNoW, btnNoH;
    private float panelX, panelY, panelW, panelH;
    private boolean yesHover = false, noHover = false;

    private UIButton primaryBtn;
    private UIButton secondaryBtn;


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
        timeLeft = 65f;
        currentlyDragging = null;

        enableHUD((int) getWorldWidth(), (int) getWorldHeight());
        ElementManager.addTextures();

        centeredBox.width = GameConstants.GRID_WIDTH;
        centeredBox.height = GameConstants.GRID_HEIGHT;

        float centerX = getWorldWidth() / 2f;
        float centerY = getWorldHeight() / 2f;
        float gridX = centerX - centeredBox.width / 2f;
        float gridY = centerY - centeredBox.height / 2f;

        // Initialize level elements first (creates switches, bulbs, etc.)
        levelManager.initializeLevel(gridX, gridY, centeredBox.width);

        // Setup win conditions based on current level
        if (LevelManager.currentLevel == 1) {
            winManager.setupLevelOneConditions();
        } else if (LevelManager.currentLevel == 2) {
            winManager.setupLevelTwoConditions();
        } else if (LevelManager.currentLevel == 3) {
            // GET THE SWITCH AFTER IT EXISTS
            Switch keySwitch = levelManager.getSwitchKey();
            winManager.setupLevelThreeConditions(keySwitch);
        } else if (LevelManager.currentLevel == 4) {
            winManager.setupLevelFourConditions();
        } else if (LevelManager.currentLevel == 5) {
            winManager.setupLevelFiveConditions();
        }
        else {
            // Handle undefined levels - return to menu or show message
            System.out.println("Level " + LevelManager.currentLevel + " not yet implemented!");
            LevelManager.currentLevel = 1;
            GameApp.switchScreen("MainMenuScreen");
        }

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
        GameApp.clearScreen();
        HelperMethods.setBackground(LevelManager.currentLevel);
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
            return;
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
            winManager.checkConnections(connectionManager, levelManager.getPorts(), levelManager.getBulbs(), levelManager.getExtensionCords(), levelManager.getPlugs(), levelManager.getRegulators(), levelManager.getVoltagePorts(), toolManager.getPlacedTools());

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
        GridManager.drawGrid(gridX, gridY, centeredBox.width);
        connectionManager.drawWirePathsTextures();
        levelManager.drawElements();
        toolManager.drawTools();
        drawHearts(gridX, gridY);
        drawTimer(gridX, gridY);
        drawHintsButton(gridX, gridY);
        GameApp.endSpriteRendering();

        GameApp.startShapeRenderingFilled();
        drawToolBoxes(gridX, gridY);
        connectionManager.drawWirePathsPreview();
        GameApp.endShapeRendering();
    }

    private void handleInput(float gridX, float gridY) {
        float mouseX = getMouseX();
        float mouseY = getMouseY();
        float cellSize = centeredBox.width / GameConstants.GRID_SIZE;

        // ===== LEFT CLICK - WIRE DRAWING & TOOL DRAGGING =====
        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            boolean clickedElement = false;

            // Check bulbs - can start/finish wire connections
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

            // Check switches - can start/finish wire connections (toggle moved to right-click)
            if (!clickedElement) {
                for (Switch sw : levelManager.getSwitches()) {
                    if (sw.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(sw);
                        } else {
                            connectionManager.startBuilding(sw, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check placed tools - can start/finish wire connections
            if (!clickedElement) {
                for (Tool tool : toolManager.getPlacedTools()) {
                    if (tool.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(tool);
                        } else {
                            connectionManager.startBuilding(tool, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check ports - can start/finish wire connections
            if (!clickedElement) {
                for (WirePort port : levelManager.getPorts()) {
                    if (port.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(port);
                        } else {
                            connectionManager.startBuilding(port, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check extension cords - can start/finish wire connections
            if (!clickedElement) {
                for (ExtensionCord cord : levelManager.getExtensionCords()) {
                    if (cord.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(cord);
                        } else {
                            connectionManager.startBuilding(cord, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // Check plugs - can start/finish wire connections
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

            if (!clickedElement) {
                for (VoltageRegulator regulator : levelManager.getRegulators()) {
                    if (regulator.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(regulator);
                        } else {
                            connectionManager.startBuilding(regulator, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            if (!clickedElement) {
                for (VoltagePort vPort : levelManager.getVoltagePorts()) {
                    if (vPort.contains(mouseX, mouseY)) {
                        if (connectionManager.isBuilding()) {
                            connectionManager.finishBuilding(vPort);
                        } else {
                            connectionManager.startBuilding(vPort, gridX, gridY, cellSize);
                        }
                        clickedElement = true;
                        break;
                    }
                }
            }

            // If clicked on grid tile while building wire
            if (!clickedElement && connectionManager.isBuilding() &&
                    mouseX > gridX && mouseX < gridX + centeredBox.width &&
                    mouseY > gridY && mouseY < gridY + centeredBox.height) {

                int gridCellX = (int) ((mouseX - gridX) / cellSize);
                int gridCellY = (int) ((mouseY - gridY) / cellSize);

                boolean added = connectionManager.addTileToPath(gridCellX, gridCellY);
                if (!added) {
                    System.out.println("Tile not adjacent to path!");
                }
            }

            // If not wiring and not clicking an element, check for tool dragging
            if (!clickedElement && !connectionManager.isBuilding()) {
                currentlyDragging = toolManager.getToolAtPosition(mouseX, mouseY);
            }
        }

        // ===== RIGHT CLICK - TOGGLE SWITCHES & CANCEL WIRING =====
        if (GameApp.isButtonJustPressed(Input.Buttons.RIGHT)) {
            boolean clickedSwitch = false;

            // Check if clicking on a switch to toggle it
            if (!connectionManager.isBuilding()) {
                for (Switch sw : levelManager.getSwitches()) {
                    if (sw.contains(mouseX, mouseY)) {
                        sw.toggle();
                        clickedSwitch = true;
                        System.out.println("Switch toggled!");
                        break;
                    }
                }
            }

            // If not toggling a switch, handle other right-click actions
            if (!clickedSwitch) {
                if (connectionManager.isBuilding()) {
                    // Cancel wire building
                    connectionManager.cancelBuilding();
                    System.out.println("Wire building cancelled");
                } else {
                    // Try to repair broken wire
                    connectionManager.repairWireAt(mouseX, mouseY);
                }
            }
        }

        // ===== TOOL DRAGGING =====
        if (GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            currentlyDragging.x = mouseX - currentlyDragging.width / 2;
            currentlyDragging.y = mouseY - currentlyDragging.height / 2;
        }

        // ===== TOOL PLACEMENT =====
        if (!GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            if (mouseX > gridX && mouseX < gridX + centeredBox.width &&
                    mouseY > gridY && mouseY < gridY + centeredBox.height) {

                int gridCellX = (int) ((mouseX - gridX) / cellSize);
                int gridCellY = (int) ((mouseY - gridY) / cellSize);

                // Check if cell is occupied by level element
                if (levelManager.isCellOccupied(gridCellX, gridCellY, gridX, gridY, cellSize)) {
                    toolManager.returnToolToToolbox(currentlyDragging);
                } else {
                    // Try to place tool
                    boolean placementSuccessful = toolManager.placeTool(
                            currentlyDragging, gridX, gridY, mouseX, mouseY, connectionManager);
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

    private void renderDimmedBackground() {
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

        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(0, 0, getWorldWidth(), getWorldHeight(), new Color(0, 0, 0, 0.7f));
        GameApp.endShapeRendering();
    }

    private void renderPanel(float x, float y, float w, float h) {
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(x, y, w, h, "gray-900");
        GameApp.endShapeRendering();
    }

    private void renderStats(
            float startY,
            float panelX,
            boolean showTitle,
            String title,
            String titleColor,
            ArrayList<Stat> stats
    ) {
        float y = startY;
        float lineHeight = 35f;

        GameApp.startSpriteRendering();

        if (showTitle) {
            GameApp.drawText("pixel_timer", title, panelX + 80f, y, titleColor);
            y -= lineHeight;
        }

        for (Stat s : stats) {
            if (s.required() <= 0) continue;

            boolean ok = s.connected() >= s.required();
            String color = ok ? "green-400" : "red-400";
            String status = ok ? "✔" : "✖";


            GameApp.drawText(
                    "pixel_timer",
                    status + " " + s.label() + ": " + s.connected() + "/" + s.required(),
                    panelX + 100f,
                    y,
                    color
            );
            y -= lineHeight;
        }

        GameApp.endSpriteRendering();
    }

    private void renderEndScreen() {
        // --- Background ---
        renderDimmedBackground();

        // --- Panel ---
        float panelW = 900;
        float panelH = 580;
        float panelX = getWorldWidth() / 2f - panelW / 2f;
        float panelY = getWorldHeight() / 2f - panelH / 2f;

        renderPanel(panelX, panelY, panelW, panelH);

        float mouseX = getMouseX();
        float mouseY = getMouseY();

        // ================= TITLE =================
        float titleY = panelY + panelH - 60;

        GameApp.startSpriteRendering();

        // Shadow effect for title
        GameApp.drawTextCentered(
                "pixel_timer",
                wonLevel ? "LEVEL COMPLETE!" : "TIME'S UP!",
                panelX + panelW / 2f + 3,
                titleY - 3,
                "black"
        );

        // Main title
        GameApp.drawTextCentered(
                "pixel_timer",
                wonLevel ? "LEVEL COMPLETE!" : "TIME'S UP!",
                panelX + panelW / 2f,
                titleY,
                wonLevel ? "green-400" : "red-400"
        );
        GameApp.endSpriteRendering();

        // ================= DIVIDER LINE =================
        float dividerY = titleY - 50;
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(panelX + 80, dividerY, panelW - 160, 2, wonLevel ? "green-400" : "red-400");
        GameApp.endShapeRendering();

        // ================= INFO ROW =================
        float infoY = dividerY - 45;
        float lineSpacing = 40f;

        int heartsLost = winManager.calculateHeartsLost();
        int seconds = (int) timeLeft;

        GameApp.startSpriteRendering();

        // Time display
        GameApp.drawText(
                "pixel_timer",
                "Time: " + String.format("%02d:%02d", seconds / 60, seconds % 60),
                panelX + 100,
                infoY,
                seconds > 30 ? "green-400" : "orange-400"
        );

        // Hearts display
        String heartsText = "Hearts Lost: " + heartsLost + " / 3";
        String heartsColor = heartsLost == 0 ? "green-400" : (heartsLost >= 2 ? "red-400" : "orange-400");
        GameApp.drawText(
                "pixel_timer",
                heartsText,
                panelX + 100,
                infoY - lineSpacing,
                heartsColor
        );

        GameApp.endSpriteRendering();

        // ================= STATS SECTION =================
        float statsY = infoY - lineSpacing - 50f;
        float lineH = 36;

        GameApp.startSpriteRendering();
        GameApp.drawText(
                "pixel_timer",
                wonLevel ? "Connections:" : "Missing:",
                panelX + 100,
                statsY,
                wonLevel ? "yellow-400" : "red-400"
        );
        GameApp.endSpriteRendering();

        statsY -= 45;

        Stat[] stats = {
                new Stat("Yellow Bulbs", winManager.getYellowConnected(), winManager.getYellowRequired()),
                new Stat("Red Ports", winManager.getRedConnected(), winManager.getRedRequired()),
                new Stat("Blue Ports", winManager.getBlueConnected(), winManager.getBlueRequired()),
                new Stat("Green Ports", winManager.getGreenConnected(), winManager.getGreenRequired()),
                new Stat("Orange Ports", winManager.getOrangeConnected(), winManager.getOrangeRequired()),
                new Stat("Extension Cords", winManager.getExtensionCordsConnected(), winManager.getExtensionCordsRequired()),
                new Stat("Plugs", winManager.getPlugsConnected(), winManager.getPlugsRequired()),
                new Stat("Regulators", winManager.getRegulatorsConnected(), winManager.getRegulatorsRequired()),
                new Stat("Voltage Ports", winManager.getWhiteConnected(), winManager.getWhiteRequired())
        };

        GameApp.startSpriteRendering();
        for (Stat s : stats) {
            if (s.required() <= 0) continue;

            boolean ok = s.connected() >= s.required();
            String color = ok ? "green-400" : "red-400";

            // Use simple ASCII characters that are guaranteed to work
            String icon = ok ? "[OK]" : "[X]";

            // Draw icon
            GameApp.drawText(
                    "pixel_timer",
                    icon,
                    panelX + 75,
                    statsY,
                    color
            );

            // Draw label
            GameApp.drawText(
                    "pixel_timer",
                    s.label(),
                    panelX + 200,
                    statsY,
                    "white"
            );

            // Draw progress with better spacing
            String progress = s.connected() + "/" + s.required();
            GameApp.drawText(
                    "pixel_timer",
                    progress,
                    panelX + panelW - 180,
                    statsY,
                    color
            );

            statsY -= lineH;
        }
        GameApp.endSpriteRendering();

        // ================= BUTTONS =================
        float btnY = panelY + 55;

        primaryBtn = new UIButton(
                panelX + panelW / 2f - 280 - 15,
                btnY,
                280,
                85,
                wonLevel ? "NEXT LEVEL" : "TRY AGAIN",
                wonLevel ? "green-700" : "yellow-700",
                wonLevel ? "green-500" : "yellow-500"
        );

        secondaryBtn = new UIButton(
                panelX + panelW / 2f + 15,
                btnY,
                220,
                70,
                "MAIN MENU",
                "gray-700",
                "gray-500"
        );

        primaryBtn.render(mouseX, mouseY);
        secondaryBtn.render(mouseX, mouseY);

        // ================= PERFORMANCE RATING (if won) =================
        if (wonLevel) {
            float ratingY = btnY + 100;
            String rating = "";
            String ratingColor = "";

            if (heartsLost == 0 && seconds > 40) {
                rating = "*** PERFECT! ***";
                ratingColor = "yellow-400";
            } else if (heartsLost <= 1 && seconds > 20) {
                rating = "** EXCELLENT! **";
                ratingColor = "green-400";
            } else if (heartsLost <= 2) {
                rating = "* GOOD JOB! *";
                ratingColor = "blue-400";
            } else {
                rating = "COMPLETED";
                ratingColor = "white";
            }

            GameApp.startSpriteRendering();
            GameApp.drawTextCentered(
                    "pixel_timer",
                    rating,
                    panelX + panelW / 2f,
                    ratingY,
                    ratingColor
            );
            GameApp.endSpriteRendering();
        }
    }



    private void renderEndScreenClick() {
        if (!GameApp.isButtonJustPressed(Input.Buttons.LEFT)) return;

        float mx = getMouseX();
        float my = getMouseY();

        if (primaryBtn != null && primaryBtn.isHovered(mx, my)) {
            if (wonLevel) {
                LevelManager.currentLevel++;
                GameApp.switchScreen("YourGameScreen");
            } else {
                GameApp.switchScreen("YourGameScreen");
            }
        }

        if (secondaryBtn != null && secondaryBtn.isHovered(mx, my)) {
            GameApp.switchScreen("MainMenuScreen");
        }
    }


    @Override
    public void hide() {
        // Cleanup if needed
        GameApp.disposeUIElements();
    }
}