package model.game;


import model.enums.Property;

import java.util.Map;
import java.util.Set;

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
            if( propertyValue!=null && ((value.equals(0) && value.equals(propertyValue)) ||
                    (value.compareTo(0)>0 && value.compareTo(propertyValue)<=0)
            )) continue;   //Überprüfen ob der gespeicherte Zusatzwert gleich dem Zusatzwert in dem RuleElement ist
            else return false;
        }
        return true;
    }

    public boolean matches(Feld field) {
       /* System.out.println("Token " + field.getToken() + " == " + token.toString()+ ". Zusatzwerte stimmen überein ist ");
        System.out.println((compareValues(field)) + "\n");
        System.out.println("Tokens stimmen überein ist: \n");
        System.out.println(token.matches(field.getToken()));*/
        return ( token.matches(field.getToken()) && compareValues(field));
    }

    public TokenMatcher getToken() {
        return this.token;
    }

    public Map<Property, Integer> getValues() {
        return this.values;
    }
}
