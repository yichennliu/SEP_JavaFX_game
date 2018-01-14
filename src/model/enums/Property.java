package model.enums;

public enum Property {
    MOVED(false), FALLING(false), LOOSE(false), SLIPPERY(false), PUSHABLE(false), BAM(false), BAMRICH(false), DIRECTION(false),
    A(false), B(false), C(false), D(false),
    GEMS(true), TICKS(true), X(true), Y(true), Z(true);

    private final boolean global;

    Property(boolean global) {
        this.global = global;
    }

    public boolean isGlobal() {
        return this.global;
    }
}
