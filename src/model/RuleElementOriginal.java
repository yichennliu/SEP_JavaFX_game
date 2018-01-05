package model;

import model.enums.Property;

import java.util.Map;

public class RuleElementOriginal {
    private TokenMatcher token;
    private Map<Property,Integer> values;

    public RuleElementOriginal(TokenMatcher token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }

    public boolean matches(Feld field) {
        return false; // TODO: implement
    }
}
