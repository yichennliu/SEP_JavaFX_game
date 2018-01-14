package model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InputDirection {
    GOUP(true, Neighbour.TOP), GORIGHT(true, Neighbour.RIGHT),
    GODOWN(true, Neighbour.BOTTOM), GOLEFT(true, Neighbour.LEFT),

    DIGUP(false, Neighbour.TOP), DIGRIGHT(false, Neighbour.RIGHT),
    DIGDOWN(false, Neighbour.BOTTOM), DIGLEFT(false, Neighbour.LEFT);

    private final boolean go;
    private final Neighbour neighbour;

    InputDirection(boolean go, Neighbour neighbour) {
        this.go = go;
        this.neighbour = neighbour;
    }

    public boolean isGo() {
        return this.go;
    }

    public boolean isDigOnly() {
        return !this.go;
    }

    public Neighbour getNeighbour() {
        return neighbour;
    }
}
