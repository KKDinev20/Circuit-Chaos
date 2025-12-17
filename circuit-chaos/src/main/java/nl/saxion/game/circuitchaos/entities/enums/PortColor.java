package nl.saxion.game.circuitchaos.entities.enums;

public enum PortColor {
    RED("red port"),
    BLUE("blue port"),
    GREEN("green port"),
    YELLOW("yellow port");
    //PURPLE("purple port");

    private final String textureName;

    PortColor(String textureName) {
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }
}