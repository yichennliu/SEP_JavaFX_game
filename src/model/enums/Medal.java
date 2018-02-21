package model.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.GameView;

public enum Medal {
    BRONZE("Bronze"), SILVER("Silber"), GOLD("Gold");
    private String displayName;

    Medal (String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    public Image getMedalImage(){
        return new Image("view/images/"+this.name()+".png");
    }

}
