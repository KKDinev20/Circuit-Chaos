package nl.saxion.game.circuitchaos.entities.enums;

public enum ObjectType {
    // could be used for different type of tools
    OBJECT(2);

    public final int maxPlacements;

    ObjectType(int maxPlacements) {
        this.maxPlacements = maxPlacements;
    }
}
