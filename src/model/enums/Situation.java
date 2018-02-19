package model.enums;

import model.game.Rule;
import org.omg.CORBA.Any;

import static model.enums.InputDirection.*;

public enum Situation {
    ANY, RARE, LEFT, RIGHT,
    UP, DOWN, METALEFT,
    METARIGHT, METAUP, METADOWN;

    private InputDirection inputD;


   /* public FieldDirection getNeighbour(){
        if(inputD != null)
            return inputD.getFieldDirection();
        return null;
    }*/
}
