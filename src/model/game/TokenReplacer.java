package model.game;

import model.enums.Token;

import java.util.List;

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

    public Token getToken(){
        return this.token;
    }

    public void replaceToken(Feld feld, List<Feld> feldlist){
        if(index!=null){
            feld.setToken(feldlist.get(index).getToken());
        }
        else {
            feld.setToken(this.token);
        }
    }
    public Integer getIndex() {
        return this.index;
    }
}
