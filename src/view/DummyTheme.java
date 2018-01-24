package view;

import javafx.scene.image.Image;
import model.enums.Token;
import model.themeEditor.HashMap2D;
import model.themeEditor.SpriteSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DummyTheme {

private Theme theme;

public DummyTheme(){
    HashMap2D<Token,Theme.FeldType,List<SpriteSheet>> images = new HashMap2D();
    images.put(Token.ME,Theme.FeldType.IDLE, Arrays.asList(new SpriteSheet(new Image("view/images/_Me.png"),Theme.FeldType.IDLE,168)));
    images.put(Token.MUD,Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/mud.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.GEM, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/diamand.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.FIRE, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/fire.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.WALL, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/wall_Mitte.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.WALL, Theme.FeldType.ONEEDGE, Arrays.asList(new SpriteSheet(new Image("view/images/wall_mitte_oben.png"),Theme.FeldType.ONEEDGE,168)));
    images.put(Token.WALL, Theme.FeldType.TWOEDGE, Arrays.asList(new SpriteSheet(new Image("view/images/wall_Mitte.png"),Theme.FeldType.TWOEDGE,168)));
    images.put(Token.WALL, Theme.FeldType.TWOEDGE_CORNER, Arrays.asList(new SpriteSheet(new Image("view/images/wall_links_oben.png"),Theme.FeldType.TWOEDGE_CORNER,168)));
    images.put(Token.WALL, Theme.FeldType.THREEEDGE, Arrays.asList(new SpriteSheet(new Image("view/images/wall_Mitte.png"),Theme.FeldType.THREEEDGE,168)));
    images.put(Token.GHOSTLING, Theme.FeldType.IDLE, Arrays.asList(new SpriteSheet(new Image("view/images/ape.png"),Theme.FeldType.IDLE,168)));
    images.put(Token.NORTHTHING, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/e_northling.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.WESTTHING, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/e_westling.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.SOUTHTHING, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/e_southling.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.EASTTHING, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/eastling.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.XLING, Theme.FeldType.IDLE, Arrays.asList(new SpriteSheet(new Image("view/images/snake.png"),Theme.FeldType.IDLE,168)));
    images.put(Token.SAND, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/sand.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.SIEVE, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/sieve.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.BLOCKLING, Theme.FeldType.IDLE, Arrays.asList(new SpriteSheet(new Image("view/images/spider.png"),Theme.FeldType.IDLE,168)));
    images.put(Token.POT, Theme.FeldType.FOUREDGE, Arrays.asList(new SpriteSheet(new Image("view/images/kessel.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.STONE, Theme.FeldType.TWOEDGE, Arrays.asList(new SpriteSheet(new Image("view/images/stone_mitte.png"),Theme.FeldType.FOUREDGE,168)));
    images.put(Token.STONE, Theme.FeldType.TWOEDGE_CORNER, Arrays.asList(new SpriteSheet(new Image("view/images/stone_links.png"),Theme.FeldType.TWOEDGE_CORNER,168)));
    images.put(Token.STONE, Theme.FeldType.ONEEDGE, Arrays.asList(new SpriteSheet(new Image("view/images/stone_oben.png"),Theme.FeldType.ONEEDGE,168)));

   this.theme = new Theme(images,"Test-Theme");
}

    public  Theme getTheme(){
       return this.theme;
    }
}
