package model.enums;

import model.Feld;

public enum Neighbour {
    LEFT(0,-1), LEFTTOP(-1,-1), LEFTBOTTOM(1,-1), RIGHT(0,1), RIGHTTOP(-1,1), RIGHTBOTTOM(1,1), TOP(-1,0), BOTTOM(1,0);

    private final int rowOffset;
    private final int columnOffset;

    Neighbour(int rowOffset, int columnOffset) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    public int getRowOffset() {
        return this.rowOffset;
    }

    public int getColumnOffset() {
        return this.columnOffset;
    }
}
