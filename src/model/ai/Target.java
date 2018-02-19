package model.ai;

import javafx.geometry.Point2D;
import model.enums.Token;
import model.game.Feld;

public class Target {

    private Token token;
    private Point2D pos;

    public Target(Feld feld){
        this.token = feld.getToken();
        this.pos = new Point2D(feld.getColumn(),feld.getRow());
    }

    public int getRow(){
        return (int) pos.getY();
    }

    public int getColumn(){
        return (int) pos.getX();
    }

    public boolean isToken(Token token){
        return this.token == token;
    }

    public boolean equals(Feld feld){
        return ((int) pos.getX() == feld.getColumn() &&
                (int) pos.getY() == feld.getRow() &&
                feld.getToken() == this.token);
    }
}
