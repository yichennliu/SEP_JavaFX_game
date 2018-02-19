package model.ai;

import jdk.internal.util.xml.impl.Input;
import model.enums.InputDirection;
import model.game.Feld;
import model.game.Level;

public class Breadcrumb {

    private Level level;
    private Breadcrumb from;
    private int waittime;
    private Feld currentFeld;
    private int step;
    private InputDirection inputDirection;

    public Breadcrumb(Feld currentFeld, Breadcrumb from, InputDirection inputDirection, Level level, int waittime, int step){
        this.currentFeld = currentFeld;
        this.inputDirection = inputDirection;
        this.step = step;
        this.level = level;
        this.from = from;
        this.waittime = waittime;
    }

    public InputDirection getInputDirection() {
        return inputDirection;
    }

    public int getStep(){
        return this.step;
    }

    public Level getLevel() {
        return level;
    }

    public Feld getCurrentFeld() {
        return currentFeld;
    }

    public Breadcrumb getFrom() {
        return from;
    }

    public int getWaittime() {
        return waittime;
    }
}
