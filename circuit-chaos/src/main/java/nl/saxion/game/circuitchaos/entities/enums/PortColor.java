package nl.saxion.game.circuitchaos.entities.enums;

public enum PortColor {
    RED("red port"),
    BLUE("blue port"),
    GREEN("green port"),
    YELLOW("yellow port"),
    ORANGE("orange port"),
    PURPLE("purple port"),
    PINK("pink port"),
    BLACK("black port"),
    WHITE("white port");

    private final String textureName;

    PortColor(String textureName) {
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }
}