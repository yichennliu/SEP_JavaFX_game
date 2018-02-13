package model.enums;

public enum Direction {
    EAST(Neighbour.RIGHT), WEST(Neighbour.LEFT), NORTH(Neighbour.TOP), SOUTH(Neighbour.BOTTOM);

    private final Neighbour neighbour;

    Direction(Neighbour neighbour) {
        this.neighbour = neighbour;
    }

    public Neighbour getNeighbour() {
        return neighbour;
    }
}
