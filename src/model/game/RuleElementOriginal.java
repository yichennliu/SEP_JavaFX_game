package model.game;


import model.enums.Property;
import java.util.Map;


public class RuleElementOriginal {
    private TokenMatcher token;
    private Map<Property,Integer> values;

    public RuleElementOriginal(TokenMatcher token, Map<Property,Integer> values){
        this.token = token;
        this.values = values;
    }
    /*Vergleicht die Zusatzwerte miteiander -> true, wenn alle Zusatzwerte übereinstimmen*/
    private boolean compareValues(Feld field){

        for(Map.Entry<Property,Integer> entry: values.entrySet()){
            Property prop = entry.getKey();
            Integer value = entry.getValue();
            Integer propertyValue = field.getPropertyValue(prop);  //Holen des Zusatzwertes aus dem aktuellen Feld
            if( (value.equals(0) && (propertyValue==null || propertyValue.equals(0))) ||
                    (value.intValue()>0 && propertyValue.intValue()>=value.intValue())
            ) continue;   //Überprüfen ob der gespeicherte Zusatzwert gleich dem Zusatzwert in dem RuleElement ist
            else return false;
        }
        return true;
    }

    public boolean matches(Feld field) {
        return ( token.matches(field.getToken()) && compareValues(field));
    }

    public TokenMatcher getToken() {
        return this.token;
    }

    public Map<Property, Integer> getValues() {
        return this.values;
    }
}
