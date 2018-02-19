package model.game;

import model.enums.Property;

import java.util.List;
import java.util.Map;

public class RuleElementResult {
    private TokenReplacer token;
    private Map<Property, Integer> values;

    public RuleElementResult(TokenReplacer token, Map<Property, Integer> values) {
        this.token = token;
        this.values = values;
    }

    public TokenReplacer getToken() {
        return this.token;
    }

    public Map<Property, Integer> getValues() {
        return this.values;
    }

    public void replace(Feld feld, List<Feld> feldList) {
        this.token.replaceToken(feld, feldList);
        for (Map.Entry<Property, Integer> entry : values.entrySet()) {
            Integer feldProp = feld.getPropertyValue(entry.getKey());
            Integer thisPropVal = entry.getValue();
            if( feldProp!=null && thisPropVal>0){
                Integer newVal = feldProp+thisPropVal;
                feld.setPropertyValue(entry.getKey(),(newVal>=0) ? newVal : 0);
            }
            else if(feldProp==null){
                Integer newVal = (entry.getValue()>=0) ? entry.getValue() : 0;
                feld.setPropertyValue(entry.getKey(), newVal);
            }
        }
    }
}

