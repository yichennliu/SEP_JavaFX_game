package model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InputDirection {
    GOUP(true, FieldDirection.TOP, Situation.UP), GORIGHT(true, FieldDirection.RIGHT, Situation.RIGHT),
    GODOWN(true, FieldDirection.BOTTOM, Situation.DOWN), GOLEFT(true, FieldDirection.LEFT, Situation.LEFT),

    DIGUP(false, FieldDirection.TOP,Situation.METAUP), DIGRIGHT(false, FieldDirection.RIGHT,Situation.METARIGHT),
    DIGDOWN(false, FieldDirection.BOTTOM,Situation.METARIGHT), DIGLEFT(false, FieldDirection.LEFT,Situation.METALEFT);

    private final boolean go;
    private final FieldDirection fieldDirection;
    private final Situation situation;

    InputDirection(boolean go, FieldDirection fieldDirection, Situation situation) {
        this.go = go;
        this.fieldDirection = fieldDirection;
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

    public FieldDirection getFieldDirection() {
        return fieldDirection;
    }
}
