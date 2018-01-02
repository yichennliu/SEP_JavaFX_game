import java.util.Map;

public class Feld {
    private Token token;
    private Map<Property, Integer> properties;

    public Token getToken(){
        return this.token;
    }

    public Feld(Token token, Map<Property, Integer> properties){
        this.token= token;
        this.properties = properties;
    }
}
