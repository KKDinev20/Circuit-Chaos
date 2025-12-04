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
    private ExampleObject currentlyDragging = null;

    public YourGameScreen() {
        super(1280, 720);
        toolManager = new ToolManager();
    }

    @Override
    public void show() {
        centeredBox.width = GameConstants.GRID_WIDTH;
        centeredBox.height = GameConstants.GRID_HEIGHT;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        float centerX = getWorldWidth() / 2;
        float centerY = getWorldHeight() / 2;
        float gridX = centerX - centeredBox.width / 2;
        float gridY = centerY - centeredBox.height / 2;

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

    @Override
    public void hide() {
        // Cleanup if needed
    }
}