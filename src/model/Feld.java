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
    private Level level;

    public Feld(Token token, int column, int row) {
        this(token, new HashMap<Property, Integer>(), column, row);
    }

    public Feld(Token token, Map<Property, Integer> properties, int column, int row){
        this.token = token;
        this.properties = properties;
        this.neighbours = new HashMap<Neighbour,Feld>();
        this.column = column;
        this.row = row;
    }

    public void setToken(Token token){
        this.token = token;
    }

    public Token getToken(){
        return this.token;
    }

    public void setNeighbour(Neighbour neighbour, Feld field){
        this.neighbours.put(neighbour,field);
    }
    public Feld getNeighbour(Neighbour neighbour) {
        return this.neighbours.get(neighbour);
    }

    public String toString() {
        return this.token.name();
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /** @return value associated to local or global property */
    public Integer getPropertyValue(Property property){
        if (property.isGlobal()) {
            return this.level.getPropertyValue(property);
        } else {
            return this.properties.get(property) == null
                    ? 0
                    : this.properties.get(property);
        }
    }

    /** sets local or global property */
    public void setPropertyValue(Property property, int value){
        if (property.isGlobal()) {
            this.level.setPropertyValue(property, value);
        } else {
            this.properties.put(property, value);
        }
    }

    /** if property != 0 */
    public boolean hasProperty(Property property) {
        return this.getPropertyValue(property) != 0;
    }

    /** set property = 1 */
    public void setProperty(Property property) {
        this.setPropertyValue(property, 1);
    }

    /** set property = 0 */
    public void resetProperty(Property property) {
        this.setPropertyValue(property, 0);
    }
}
