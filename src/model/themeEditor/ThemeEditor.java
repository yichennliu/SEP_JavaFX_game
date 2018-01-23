package model.themeEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.enums.Token;
import view.Theme;
import model.themeEditor.HashMap2D;

import java.util.*;

public class ThemeEditor {

    private HashMap2D<Token, Theme.FeldType, ObservableList<SpriteSheet>> sprites;

    public ThemeEditor() {
        initMaps();
    }

    public void loadTheme(Theme theme) {
        this.initMaps();
        HashMap2D<Token, Theme.FeldType, List<SpriteSheet>> themeImageMap = theme.getImages();
        for (Token t : Token.values()) {
            for (Theme.FeldType f : Theme.FeldType.values()) {
                List<SpriteSheet> themeImageList = themeImageMap.get(t, f);
                ObservableList<SpriteSheet> thisImageList = this.sprites.get(t, f);
                if (themeImageList != null) {
                    for (SpriteSheet img : themeImageList) {
                        thisImageList.add(img);
                    }
                }
            }
        }

    }

    public Theme makeTheme(){
        HashMap2D<Token, Theme.FeldType, List<SpriteSheet>> themeSprites = new HashMap2D();
        for(Token t:Token.values()){
            for(Theme.FeldType f: Theme.FeldType.values()){
                List<SpriteSheet> variations = sprites.get(t,f);
                themeSprites.put(t,f,variations);
            }
        }
        return new Theme(themeSprites,"themeName");
    }

    private void initMaps() {
        sprites = new HashMap2D<Token, Theme.FeldType, ObservableList<SpriteSheet>>();
        for (Token t : Token.values()) {
            for (Theme.FeldType f : Theme.FeldType.values()) {
                this.sprites.put(t,f,FXCollections.observableArrayList());
            }
        }
    }

    public HashMap2D<Token, Theme.FeldType, ObservableList<SpriteSheet>> getSprites(){
        return this.sprites;
    }

}
