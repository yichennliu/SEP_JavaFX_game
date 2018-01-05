package model;

import model.enums.Token;

import java.util.List;

public class TokenMatcher {

    private List<Token> tokens;
    private boolean any = true;
    private Integer index;

    public TokenMatcher() {/* any = true */}

    public TokenMatcher(List<Token> tokens){
        if (tokens == null) throw new RuntimeException("Tokens may not be null");
        this.any = false;
        this.tokens = tokens;
    }

    public TokenMatcher(int index){
        this.any = false;
        this.index = index;
    }

    public boolean matches(Token token, List<Token> examinedTokens){
        return false; // TODO: implement
    }

}
