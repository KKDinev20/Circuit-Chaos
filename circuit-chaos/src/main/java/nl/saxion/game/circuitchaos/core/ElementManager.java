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



        loaded = true;
    }
}