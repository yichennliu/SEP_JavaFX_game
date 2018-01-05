package model;

import model.enums.Property;

import java.util.List;
import java.util.Map;

public class RuleElement {

    private TokenMatcher token;
    private Map<Property,Integer> values;

    public RuleElement(TokenMatcher token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }

    public boolean matches(Feld field) {
        return false; // TODO: implement
    }
}
