package model;

import java.util.List;

public class User {

    private String name;
    private String lastlevel;
    private MedaillenSammlung medaillen;


    public User(String name, String lastlevel , MedaillenSammlung medaillen){

        this.name = name;
        this.lastlevel = lastlevel;
        this.medaillen = medaillen;

    }
}
