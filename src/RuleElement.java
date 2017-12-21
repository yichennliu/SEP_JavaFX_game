import java.util.Map;

public class RuleElement {

    private List<TokenMatcher> tokens;
    private Map<Token,Integer> values;

    public RuleElement(List<TokenMatcher> tokens, Map<Token,Integer> values){
        this.tokens = tokens;
        this.values = values;
    }
}
