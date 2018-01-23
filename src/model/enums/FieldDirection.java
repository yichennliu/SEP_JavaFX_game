package model.enums;

public enum FieldDirection {
    RIGHTTOP(-1,1, null, 1), RIGHT(0,1, 1, 2), RIGHTBOTTOM(1,1, null, 3), BOTTOM(1,0, 4, 4),
    LEFTBOTTOM(1,-1, null, 5), LEFT(0,-1, 3, 6), LEFTTOP(-1,-1, null, 7), TOP(-1,0, 2, 8);

    private final int rowOffset;
    private final int columnOffset;
    private final Integer direction; // as in Property.DIRECTION
    private final int clockWiseOrder;

    FieldDirection(int rowOffset, int columnOffset, Integer direction, int clockWiseOrder) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
        this.direction = direction;
        this.clockWiseOrder = clockWiseOrder;
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    public int getColumnOffset() {
        return this.columnOffset;
    }

    public Integer getDirection() {
        return this.direction;
    }

    public static FieldDirection getFromDirection(int direction) {
        if (direction < 1 || direction > 4)
            throw new IllegalArgumentException("'direction' must be between 1 and 4");

        for (FieldDirection nb: FieldDirection.values()) {
            if (nb.direction != null && nb.direction == direction) {
                return nb;
            }
        }
        return null;
    }

    public static FieldDirection getRotated(FieldDirection original, FieldDirection relative) {
        int newDirection = original.clockWiseOrder + relative.clockWiseOrder;
        if (newDirection > 8) newDirection = newDirection-8;

        for (FieldDirection nb : FieldDirection.values()) {
            if (nb.clockWiseOrder == newDirection)
                return nb;
        }
        return null;
    }

}
