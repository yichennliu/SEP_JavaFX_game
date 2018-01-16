package model.themeEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.enums.Token;
import view.Theme;
import model.themeEditor.HashMap2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeEditor {

    private HashMap2D<Token, Theme.FeldType, ObservableList<Image>> imageMap;
    private HashMap2D<Token, Theme.FeldType, ObservableList<String>> pathMap;

    public ThemeEditor() {
        initMaps();
    }

    public void loadTheme(Theme theme) {
        this.initMaps();
        HashMap2D<Token, Theme.FeldType, List<Image>> themeImageMap = theme.getImages();
        for (Token t : Token.values()) {
            for (Theme.FeldType f : Theme.FeldType.values()) {
                List<Image> themeImageList = themeImageMap.get(t, f);
                ObservableList<Image> thisImageList = this.imageMap.get(t, f);
                if (themeImageList != null) {
                    for (Image img : themeImageList) {
                        thisImageList.add(img);
                    }
                }
            }
        }

    }

    private void initMaps() {
        imageMap = new HashMap2D<Token, Theme.FeldType, ObservableList<Image>>();
        pathMap = new HashMap2D<Token, Theme.FeldType, ObservableList<String>>();
        for (Token t : Token.values()) {
            for (Theme.FeldType f : Theme.FeldType.values()) {
                this.pathMap.put(t, f, FXCollections.observableArrayList());
                this.imageMap.put(t, f, FXCollections.observableArrayList());
            }
        }
    }

    public HashMap2D<Token, Theme.FeldType, ObservableList<Image>> getImageMap() {
        return this.imageMap;
    }

    public HashMap2D<Token, Theme.FeldType, ObservableList<String>> getPathMap() {
        return this.pathMap;
    }
}
