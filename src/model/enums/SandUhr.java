package model.enums;

import javafx.scene.image.Image;
import view.GameView;

public enum SandUhr {

    RED(new Image(GameView.class.getResourceAsStream("images/redSandUhr.png"))),
    YELLOW(new Image(GameView.class.getResourceAsStream("images/yellowSandUhr.png"))),
    GREEN(new Image(GameView.class.getResourceAsStream("images/greenSandUhr.png")));

    private final Image sandUhrImage;

    SandUhr(Image sandUhrImage) {

        this.sandUhrImage = sandUhrImage;
    }

    public Image getSandUhrImage() {

        return this.sandUhrImage;

    }

}


