package model.enums;

public enum Direction {
    EAST(FieldDirection.RIGHT), WEST(FieldDirection.LEFT), NORTH(FieldDirection.TOP), SOUTH(FieldDirection.BOTTOM);

    private final FieldDirection fieldDirection;

    Direction(FieldDirection fieldDirection) {
        this.fieldDirection = fieldDirection;
    }

    public FieldDirection getFieldDirection() {
        return fieldDirection;
    }
}
