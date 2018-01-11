package model;

import model.enums.Property;
import model.enums.Token;

import java.util.HashMap;
import java.util.Map;

public class Feld {
    private Token token;
    private Map<Property, Integer> properties;
    private int row;
    private int column;
    public enum Neighbour {LEFT, LEFTTOP, LEFTBOTTOM, RIGHT, RIGHTTOP, RIGHTBOTTOM, TOP, BOTTOM};
    private Map<Neighbour, Feld> neighbours;

    public Feld(Token token) {
        this(token, new HashMap<Property, Integer>());
    }

    public Feld(Token token, Map<Property, Integer> properties){
        this.token = token;
        this.properties = properties;
    }

    public void setToken(Token token){
        this.token = token;
    }

    public void setNeighbour(Neighbour neighbour, Feld field){
        this.neighbours.put(neighbour,field);
    }
    public Feld getNeighbour(Neighbour neighbour) {
        return this.neighbours.get(neighbour);
    }

    public Token getToken(){
        return this.token;
    }

    public String toString() {
        return this.token.name();
    }
}
