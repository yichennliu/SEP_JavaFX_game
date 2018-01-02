import java.util.List;
import java.util.Map;

public class RuleElement {

    private List<TokenMatcher> tokens;
    private Map<Property,Integer> values;

    public RuleElement(List<TokenMatcher> tokens, Map<Property,Integer> values){
        this.tokens = tokens;
        this.values = values;
    }
}
