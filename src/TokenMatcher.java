import java.util.List;

public class TokenMatcher {

    private List<Token> tokens;
    private Boolean any;
    private Integer index;

    public boolean matches(Token token, List<Token> examinedTokens){
        return false;
    }

    public TokenMatcher(List<Token> tokens){
        this.tokens = tokens;
    }

    public TokenMatcher(Boolean any){
        this.any = any;
    }

    public TokenMatcher (Integer index){
        this.index = index;
    }
}
