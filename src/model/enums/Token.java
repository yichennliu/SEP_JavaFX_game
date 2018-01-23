package model.enums;

public enum Token {
    ME(true), MUD(false), STONE(false), GEM(false), EXIT(false), WALL(false), BRICKS(false), PATH(false),
    EXPLOSION(false), SLIME(false), SWAPLING(true), BLOCKLING(true), XLING(true),
    GHOSTLING(true), FIRE(false), NORTHTHING(false), EASTTHING(false), SOUTHTHING(false), WESTTHING(false), POT(false), SIEVE(false), SAND(false);


    private boolean isMovable; // zeigt an, ob der Token ein Spieler oder Gegner ist

    Token(boolean movable){
        this.isMovable = movable;
    }

    public boolean isMovable(){
        return this.isMovable;
    }
}