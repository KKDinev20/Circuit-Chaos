package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.*;
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
    // TIMER
    private float timeLeft = 120f;   // 2 minutes
    private boolean timeUp = false;


    // UI positions for quit menu
    private float btnYesX, btnYesY, btnYesW, btnYesH;
    private float btnNoX, btnNoY, btnNoW, btnNoH;
    private float panelX, panelY, panelW, panelH;
    private boolean yesHover = false, noHover = false;

    public YourGameScreen() {
        super(1280, 720);
        toolManager = new ToolManager();
        levelManager = new LevelManager();
        connectionManager = new TileConnectionManager();
    }

    @Override
    public void show() {
        GameApp.addFont("pixel_timer", "fonts/PressStart2P-Regular.ttf", 20);
        GameApp.addFont("levelSelectFont", "fonts/Cause-Medium.ttf", 40);

        timeLeft = 120f;
        timeUp = false;

        enableHUD((int) getWorldWidth(), (int) getWorldHeight());
        ElementManager.addTextures(); // Load bulb textures
        GameApp.addTexture("level1", "textures/backgrounds/house.png");

        centeredBox.width = GameConstants.GRID_WIDTH;
        centeredBox.height = GameConstants.GRID_HEIGHT;

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

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (!timeUp && !showQuitMenu) {
            timeLeft -= delta;

            if (timeLeft <= 0) {
                timeLeft = 0;
                timeUp = true;
                GameApp.switchScreen("MainMenuScreen");
                return;
            }
        }

        float centerX = getWorldWidth() / 2;
        float centerY = getWorldHeight() / 2;
        float gridX = centerX - centeredBox.width / 2;
        float gridY = centerY - centeredBox.height / 2;

        if (GameApp.isKeyJustPressed(Input.Keys.ESCAPE)) {
            showQuitMenu = !showQuitMenu;
        }

        if (showQuitMenu) {
            GameApp.clearScreen(Color.BLACK);
            renderQuitMenu();
            renderQuitMenuClick();
            renderUI();
            return;
        }

        // Initialize tools if not done
        if (toolManager.getToolboxTools()[0] == null) {
            toolManager.initializeTools(gridX, gridY);
        }

        // Initialize level if not done
        levelManager.initializeLevel(gridX, gridY, centeredBox.width);

        handleInput(gridX, gridY);

        GameApp.clearScreen(Color.BLACK);

        // Update level elements (bulbs animation)
        levelManager.updateElements();
        connectionManager.updateWirePaths();

        // --- START SPRITE RENDERING (textures) ---
        GameApp.startSpriteRendering();

        // Draw background
        GameApp.drawTexture("level1", 0, 0, getWorldWidth(), getWorldHeight());

        // Draw grid
        GridManager.drawGrid(gridX, gridY, centeredBox.width);

        // Draw level elements (bulbs and ports) FIRST
        levelManager.drawElements();

        // Draw wire textures ON TOP of elements
        connectionManager.drawWirePathsTextures();

        // Draw hearts
        drawHearts(gridX, gridY);

        // Draw timer (middle top)
        drawTimer(gridX, gridY);

        // Draw hints button (as texture)
        drawHintsButton(gridX, gridY);

        GameApp.endSpriteRendering();
        // --- END SPRITE RENDERING ---

        // --- START SHAPE RENDERING (rectangles, lines) ---
        GameApp.startShapeRenderingFilled();

        // Draw UI shapes
        drawToolBoxes(gridX, gridY);

        // Draw wire building preview (cyan tiles)
        connectionManager.drawWirePathsPreview();

        // Draw tools
        toolManager.drawTools();

        GameApp.endShapeRendering();
        // --- END SHAPE RENDERING ---
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
                        // Finish connection - NO GRID PARAMETERS NEEDED
                        connectionManager.finishBuilding(bulb);
                    } else {
                        // Start new connection - ADD GRID PARAMETERS
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
        if (GameApp.isButtonJustPressed(Input.Buttons.RIGHT) ||
                GameApp.isKeyJustPressed(Input.Keys.ESCAPE)) {
            connectionManager.cancelBuilding();
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

    @Override
    public void hide() {
        // Cleanup if needed
        GameApp.disposeUIElements();
    }
}