package model.enums;

import javafx.scene.image.Image;
import view.GameView;

public enum Medal {
    BRONZE(new Image(GameView.class.getResourceAsStream("images/BRONZE.png"))),
    SILVER(new Image(GameView.class.getResourceAsStream("images/SILVER.png"))),
    GOLD(new Image(GameView.class.getResourceAsStream("images/gold.png")));

    private final Image medalimage;

    Medal (Image medalimage) {
        this.medalimage = medalimage;
    }


    public Image getMedalImage() { return this.medalimage; }

}
