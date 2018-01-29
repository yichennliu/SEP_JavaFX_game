package model.game;

import model.enums.Property;

import java.util.Map;

public class RuleElementResult {
    private TokenReplacer token;
    private Map<Property,Integer> values;

    public RuleElementResult(TokenReplacer token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }

    public TokenReplacer getToken() {
        return this.token;
    }

    public Map<Property, Integer> getValues() {
        return this.values;
    }
}
