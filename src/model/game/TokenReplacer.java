package model.game;

import model.enums.Token;

public class TokenReplacer {

    private Token token;
    private Integer index;

    public TokenReplacer(Token token){
        if (token == null) throw new RuntimeException("Token may not be null");
        this.token = token;
    }

    public TokenReplacer(int index){
        this.index = index;
    }

}
