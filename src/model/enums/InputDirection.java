package model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InputDirection {
    GOUP(true, Neighbour.TOP, Situation.UP), GORIGHT(true, Neighbour.RIGHT, Situation.RIGHT),
    GODOWN(true, Neighbour.BOTTOM, Situation.DOWN), GOLEFT(true, Neighbour.LEFT, Situation.LEFT),

    DIGUP(false, Neighbour.TOP,Situation.METAUP), DIGRIGHT(false, Neighbour.RIGHT,Situation.METARIGHT),
    DIGDOWN(false, Neighbour.BOTTOM,Situation.METARIGHT), DIGLEFT(false, Neighbour.LEFT,Situation.METALEFT);

    private final boolean go;
    private final Neighbour neighbour;
    private final Situation situation;

    InputDirection(boolean go, Neighbour neighbour, Situation situation) {
        this.go = go;
        this.neighbour = neighbour;
        this.situation = situation;
    }

    public Situation getSituation(){
        return this.situation;
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
