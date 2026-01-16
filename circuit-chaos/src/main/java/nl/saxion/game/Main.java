package nl.saxion.game;

import com.badlogic.gdx.Game;
import nl.saxion.game.circuitchaos.ui.screen.GameWinScreen;
import nl.saxion.game.circuitchaos.ui.screen.LevelSelectScreen;
import nl.saxion.game.circuitchaos.ui.screen.YourGameScreen;
import nl.saxion.game.circuitchaos.ui.screen.MainMenuScreen;
import nl.saxion.gameapp.GameApp;

public class Main {
    public static void main(String[] args) {
        // Add screens
        GameApp.addScreen("MainMenuScreen", new MainMenuScreen());
        GameApp.addScreen("LevelSelectScreen", new LevelSelectScreen());
        GameApp.addScreen("YourGameScreen", new YourGameScreen());
        GameApp.addScreen("GameWinScreen", new GameWinScreen());

        // Start game loop and show main menu screen
        GameApp.start("Circuit Chaos", 800, 450, 60, true, "MainMenuScreen");
    }

}
