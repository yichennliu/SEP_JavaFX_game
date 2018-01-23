package model.enums;

public enum InputDirection {
    GOUP(true, FieldDirection.TOP), GORIGHT(true, FieldDirection.RIGHT),
    GODOWN(true, FieldDirection.BOTTOM), GOLEFT(true, FieldDirection.LEFT),

    DIGUP(false, FieldDirection.TOP), DIGRIGHT(false, FieldDirection.RIGHT),
    DIGDOWN(false, FieldDirection.BOTTOM), DIGLEFT(false, FieldDirection.LEFT);

    private final boolean go;
    private final FieldDirection fieldDirection;

    InputDirection(boolean go, FieldDirection fieldDirection) {
        this.go = go;
        this.fieldDirection = fieldDirection;
    }

    public boolean isGo() {
        return this.go;
    }

    public boolean isDigOnly() {
        return !this.go;
    }

    public FieldDirection getFieldDirection() {
        return fieldDirection;
    }
}
