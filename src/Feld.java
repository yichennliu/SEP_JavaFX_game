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

    public Token getToken(){
        return this.token;
    }

    public String toString() {
        return this.token.name();
    }
}
