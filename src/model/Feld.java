package model;

import model.enums.Property;
import model.enums.Token;

import java.util.HashMap;
import java.util.Map;

public class Feld {
    private Token token;
    private Map<Property, Integer> properties;

    public Feld(Token token) {
        this(token, new HashMap<Property, Integer>());
    }

    public Feld(Token token, Map<Property, Integer> properties){
        this.token = token;
        this.properties = properties;
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

    public void setToken(Token token) {
        this.token = token;
    }

    public void setPropertyValue(Property property, Integer value){
        this.properties.put(property,value);
    }
}
