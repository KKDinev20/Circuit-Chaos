// TextureLoader.java - Simple, no hash maps
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

        GameApp.addTexture("red wire extension", "textures/entities/wire extension red.png");
        GameApp.addTexture("blue wire extension", "textures/entities/wire extension blue.png");
        GameApp.addTexture("green wire extension", "textures/entities/wire extension green.png");
        GameApp.addTexture("yellow wire extension", "textures/entities/wire extension yellow.png");

        GameApp.addTexture("red wire vertical", "textures/entities/wire extension red vertical.png");
        GameApp.addTexture("blue wire vertical", "textures/entities/wire extension blue vertical.png");
        GameApp.addTexture("green wire vertical", "textures/entities/wire extension green vertical.png");
        GameApp.addTexture("yellow wire vertical", "textures/entities/wire extension yellow vertical.png");

        loaded = true;
    }
}