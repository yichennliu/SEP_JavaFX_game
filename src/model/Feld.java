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
        this.neighbours = new HashMap<Neighbour,Feld>();
    }

    public void setToken(Token token){
        this.token = token;
    }

    public void setNeighbour(Neighbour neighbour, Feld field){
        System.out.println("Aufgerufen auf " + this);
        this.neighbours.put(neighbour,field);
    }
    public Feld getNeighbour(Neighbour neighbour) {
        return this.neighbours.get(neighbour);
    }

    public String toString() {
        return this.token.name();
    }

    public Token getToken(){
        return this.token;
    }

    /** returns null or value associated to property */
    public Integer getPropertyValue(Property property){
        return this.properties.get(property);
    }

    public void setPropertyValue(Property property, Integer value){
        this.properties.put(property,value);
    }
}
