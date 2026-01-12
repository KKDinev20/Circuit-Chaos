package nl.saxion.game.circuitchaos.core;

import nl.saxion.gameapp.GameApp;

public class ElementManager {
    private static boolean loaded = false;

    public static void addTextures() {
        if (loaded) return;

        // Load bulb textures
        GameApp.addTexture("bulb_on", "textures/entities/bulb on.png");
        GameApp.addTexture("bulb_off", "textures/entities/bulb off.png");

        // Load wire port textures
        GameApp.addTexture("red port", "textures/entities/red wireport.png");
        GameApp.addTexture("blue port", "textures/entities/blue wireport.png");
        GameApp.addTexture("green port", "textures/entities/green wireport.png");
        GameApp.addTexture("orange port", "textures/entities/orange wireport.png");
        GameApp.addTexture("pink port", "textures/entities/pink wireport.png");
        GameApp.addTexture("purple port", "textures/entities/purple wireport.png");
        GameApp.addTexture("black port", "textures/entities/black wireport.png");
        GameApp.addTexture("white port", "textures/entities/white wireport.png");

        // Horizontal wire textures
        GameApp.addTexture("red wire extension", "textures/entities/wire extension red.png");
        GameApp.addTexture("blue wire extension", "textures/entities/wire extension blue.png");
        GameApp.addTexture("green wire extension", "textures/entities/wire extension green.png");
        GameApp.addTexture("yellow wire extension", "textures/entities/wire extension yellow.png");
        GameApp.addTexture("orange wire extension", "textures/entities/wire extension orange.png");
        GameApp.addTexture("black wire extension", "textures/entities/wire extension black.png");
        GameApp.addTexture("pink wire extension", "textures/entities/wire extension pink.png");
        GameApp.addTexture("purple wire extension", "textures/entities/wire extension purple.png");

        // Vertical wire textures
        GameApp.addTexture("red wire vertical", "textures/entities/wire extension red vertical.png");
        GameApp.addTexture("blue wire vertical", "textures/entities/wire extension blue vertical.png");
        GameApp.addTexture("green wire vertical", "textures/entities/wire extension green vertical.png");
        GameApp.addTexture("yellow wire vertical", "textures/entities/wire extension yellow vertical.png");
        GameApp.addTexture("orange wire vertical", "textures/entities/wire extension orange vertical.png");
        GameApp.addTexture("black wire vertical", "textures/entities/wire extension black vertical.png");
        GameApp.addTexture("pink wire vertical", "textures/entities/wire extension pink vertical.png");
        GameApp.addTexture("purple wire vertical", "textures/entities/wire extension purple vertical.png");


        // Extension cord textures
        GameApp.addTexture("extension cord", "textures/entities/extension cord.png");
        GameApp.addTexture("plug", "textures/entities/plug.png");

        // Add backgrounds
        GameApp.addTexture("level1", "textures/backgrounds/house.png");
        GameApp.addTexture("level2", "textures/backgrounds/Supermarket.png");
        GameApp.addTexture("level3", "textures/backgrounds/coffee bar.png");
        GameApp.addTexture("level4", "textures/backgrounds/Mall.png");
        GameApp.addTexture("level5", "textures/backgrounds/electronics repair.png");
        GameApp.addTexture("level6", "textures/backgrounds/City power station.png");


        // Switch textures
        GameApp.addTexture("switch_on", "textures/entities/switch on.png");
        GameApp.addTexture("switch_off", "textures/entities/switch off.png");

        loaded = true;
    }
}