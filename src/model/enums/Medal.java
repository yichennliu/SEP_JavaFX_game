package model.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.GameView;

public enum Medal {
    BRONZE(new Image(GameView.class.getResourceAsStream("images/Bronze.png")),"Bronze"),
    SILVER(new Image(GameView.class.getResourceAsStream("images/Silver.png")),"Silber"),
    GOLD(new Image(GameView.class.getResourceAsStream("images/Gold.png")),"Gold");

    private String displayName;
    private final Image medalimage;

    Medal (Image medalimage, String displayName) {
        this.displayName = displayName;
        this.medalimage = medalimage;
    }

    public String getDisplayName() {
        return displayName;
    }


    public ImageView getMedalImage(){
        ImageView imageView = new ImageView(this.medalimage);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);

        return imageView;
    }

}
