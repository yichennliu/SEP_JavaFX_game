package model;

import model.enums.Property;

import java.util.Map;

public class RuleElementResult {
    private TokenReplacer token;
    private Map<Property,Integer> values;

    public RuleElementResult(TokenReplacer token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }

}
