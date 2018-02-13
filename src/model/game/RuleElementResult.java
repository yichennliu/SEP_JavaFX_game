package model.game;

import model.enums.Property;

import java.util.List;
import java.util.Map;

public class RuleElementResult {
    private TokenReplacer token;
    private Map<Property,Integer> values;

    public RuleElementResult(TokenReplacer token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }

    public void replace(Feld feld, List<Feld> feldList){
        this.token.replaceToken(feld,feldList);
        for(Map.Entry<Property,Integer> entry : values.entrySet() ){
            feld.setPropertyValue(entry.getKey(),entry.getValue());
        }

    }

}
