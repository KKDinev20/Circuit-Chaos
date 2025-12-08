package nl.saxion.game.circuitchaos.ui.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import nl.saxion.game.circuitchaos.core.*;
import nl.saxion.game.circuitchaos.entities.*;
import nl.saxion.game.circuitchaos.util.GameConstants;
import nl.saxion.gameapp.GameApp;
import nl.saxion.gameapp.screens.ScalableGameScreen;

import java.awt.*;

public class YourGameScreen extends ScalableGameScreen {
    private Box centeredBox = new Box();
    private ToolManager toolManager;
    private ExampleObject currentlyDragging = null;
    private boolean showQuitMenu = false;
    private float btnYesX, btnYesY, btnYesW, btnYesH;
    private float btnNoX, btnNoY, btnNoW, btnNoH;
    private float panelX, panelY, panelW, panelH;
    private boolean yesHover = false, noHover = false;

    public YourGameScreen() {
        super(1280, 720);
        toolManager = new ToolManager();
    }

    @Override
    public void show() {
        enableHUD((int)getWorldWidth(), (int)getWorldHeight());

        centeredBox.width = GameConstants.GRID_WIDTH;
        centeredBox.height = GameConstants.GRID_HEIGHT;

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

        if (toolManager.getToolboxTools()[0] == null) {
            toolManager.initializeTools(gridX, gridY);
        }

        handleInput(gridX, gridY);

        GameApp.clearScreen(Color.BLACK);

        GameApp.startSpriteRendering();

        GridManager.drawGrid(gridX, gridY, centeredBox.width);
        drawHearts(gridX, gridY);

        GameApp.endSpriteRendering();

        GameApp.startShapeRenderingFilled();

        drawHintsButton(gridX, gridY);
        drawToolBoxes(gridX, gridY);
        drawTimer(gridX, gridY);
        toolManager.drawTools();

        GameApp.endShapeRendering();
    }

    private void handleInput(float gridX, float gridY) {
        float mouseX = getMouseX();
        float mouseY = getMouseY();

        if (GameApp.isButtonJustPressed(Input.Buttons.LEFT)) {
            currentlyDragging = toolManager.getToolAtPosition(mouseX, mouseY);
        }

        if (GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            currentlyDragging.x = mouseX - currentlyDragging.width / 2;
            currentlyDragging.y = mouseY - currentlyDragging.height / 2;
        }

        if (!GameApp.isButtonPressed(Input.Buttons.LEFT) && currentlyDragging != null) {
            if (mouseX > gridX && mouseX < gridX + centeredBox.width &&
                    mouseY > gridY && mouseY < gridY + centeredBox.height) {

                // Try to place the tool - if placement fails (cell occupied), return to toolbox
                boolean placementSuccessful = toolManager.placeTool(currentlyDragging, gridX, gridY, mouseX, mouseY);
                if (!placementSuccessful) {
                    toolManager.returnToolToToolbox(currentlyDragging);
                }
            } else {
                // Return to toolbox if not placed in grid
                toolManager.returnToolToToolbox(currentlyDragging);
            }
            currentlyDragging = null;
        }
    }

    private void drawHearts(float gridX, float gridY) {
        float heartsX = gridX;
        float heartsY = gridY + GameConstants.GRID_HEARTS_SPACING + GameConstants.GRID_HEIGHT;

        GameApp.addTextureAtlas("heart", "textures/atlases/hearts.atlas");
        for (int i = 0; i < GameConstants.HEARTS_COUNT; i++) {
            float heartX = heartsX + (i * (GameConstants.HEARTS_SIZE + GameConstants.HEARTS_SPACING));

            GameApp.drawAtlasRegion(
                    "heart",
                    "hearts",
                    heartX,
                    heartsY,
                    GameConstants.HEARTS_SIZE,
                    GameConstants.HEARTS_SIZE
            );
        }
    }


    private void drawHintsButton(float gridX, float gridY) {
        float buttonWidth = 120f;
        float buttonHeight = 50f;
        float buttonX = gridX + centeredBox.width - buttonWidth;
        float buttonY = gridY + 30f + centeredBox.height;

        GameApp.drawRect(buttonX, buttonY, buttonWidth, buttonHeight, Color.YELLOW);
    }

    private void drawToolBoxes(float gridX, float gridY) {
        float toolY = gridY - GameConstants.GRID_TOOL_SPACING - GameConstants.TOOL_SIZE;
        float startX = gridX + 15f;

        for (int i = 0; i < GameConstants.TOOL_COUNT; i++) {
            float toolX = startX + (i * GameConstants.TOOL_SPACING);
            GameApp.drawRect(toolX, toolY, GameConstants.TOOL_SIZE, GameConstants.TOOL_SIZE, Color.DARK_GRAY);
        }
    }

    private void drawTimer(float gridX, float gridY) {
        float timerHeight = 10f;
        float timerY = gridY + centeredBox.height + 8f;

        GameApp.drawRect(gridX, timerY, centeredBox.width, timerHeight, Color.GREEN);
        GameApp.drawRect(gridX, timerY, centeredBox.width - 80f, timerHeight, Color.RED);
    }

    private void renderQuitMenu() {

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        // detect hover
        yesHover = (mx >= btnYesX && mx <= btnYesX + btnYesW &&
                my >= btnYesY && my <= btnYesY + btnYesH);

        noHover  = (mx >= btnNoX && mx <= btnNoX + btnNoW &&
                my >= btnNoY && my <= btnNoY + btnNoH);


        // ==== DRAW PANEL ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(panelX, panelY, panelW, panelH, "gray-800");
        GameApp.endShapeRendering();


        // ==== DRAW YES BUTTON ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(
                btnYesX - (yesHover ? 5 : 0),
                btnYesY - (yesHover ? 5 : 0),
                btnYesW + (yesHover ? 10 : 0),
                btnYesH + (yesHover ? 10 : 0),
                yesHover ? "blue-400" : "blue-300"
        );
        GameApp.endShapeRendering();


        // ==== DRAW NO BUTTON ====
        GameApp.startShapeRenderingFilled();
        GameApp.drawRect(
                btnNoX - (noHover ? 5 : 0),
                btnNoY - (noHover ? 5 : 0),
                btnNoW + (noHover ? 10 : 0),
                btnNoH + (noHover ? 10 : 0),
                noHover ? "red-400" : "red-300"
        );
        GameApp.endShapeRendering();


        // ==== TEXT ====
        GameApp.startSpriteRendering();

        GameApp.drawTextCentered(
                "basic",
                "Are you sure you want to quit?",
                panelX + panelW / 2f,
                panelY + panelH - 60f,
                "white"
        );

        GameApp.drawTextCentered("basic", "Yes",
                btnYesX + btnYesW / 2f,
                btnYesY + btnYesH / 2f,
                "white");

        GameApp.drawTextCentered("basic", "No",
                btnNoX + btnNoW / 2f,
                btnNoY + btnNoH / 2f,
                "white");

        GameApp.endSpriteRendering();
    }

    private void renderQuitMenuClick() {

        if (!GameApp.isButtonJustPressed(Input.Buttons.LEFT)) return;

        float[] m = windowToWorldMouse();
        float mx = m[0];
        float my = m[1];

        // YES
        if (mx >= btnYesX && mx <= btnYesX + btnYesW &&
                my >= btnYesY && my <= btnYesY + btnYesH) {

            GameApp.quit();
            return;
        }

        // NO
        if (mx >= btnNoX && mx <= btnNoX + btnNoW &&
                my >= btnNoY && my <= btnNoY + btnNoH) {

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
    }
}