package model.game;

import model.enums.Token;

import java.util.List;

public class TokenMatcher {
    private List<Token> tokens;

    /**
     * Match any token
     */
    public TokenMatcher() {}

    /**
     * Match one of the given tokens
     *
     * @param tokens non-null, non-empty
     */
    public TokenMatcher(List<Token> tokens){
        if (tokens == null || tokens.isEmpty())
            throw new RuntimeException("Tokens may not be null or empty");
        this.tokens = tokens;
    }

    public boolean matches(Token token) {
        if (this.tokens == null) {
            return true;
        } else {
            return this.tokens.contains(token);
        }
    }

    /**
     * @return Tokens, or null (= "*")
     */
    public List<Token> getTokens() {
        return tokens;
    }

}
