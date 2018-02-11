package view.animation;

import model.themeEditor.SpriteSheet;

public class StaticViewToken {

    private double x;
    private double y;
    private SpriteSheet s;
    private String name;

    public StaticViewToken(double x, double y, String name, SpriteSheet s){
        this.x =x;
        this.y = y;
        this.s =s;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public SpriteSheet getS() {
        return s;
    }
}
