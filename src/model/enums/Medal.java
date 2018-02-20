package model.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.GameView;

public enum Medal {
    BRONZE("Bronze"), SILVER("Silber"), GOLD("Gold");
    private String displayName;
    final Image goldMedalImage = new Image(GameView.class.getResourceAsStream("images/Gold.png"));
    final Image silverMedalImage = new Image(GameView.class.getResourceAsStream("images/Silver.png"));
    final Image bronzeMedalImage = new Image(GameView.class.getResourceAsStream("images/Bronze.png"));


    Medal (String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    public ImageView getMedalImage(Image medalImage){
        ImageView imageView = new ImageView(medalImage);
        switch (this) {
            case GOLD:
                imageView = new ImageView(goldMedalImage);
                break;

            case SILVER:
                imageView = new ImageView(silverMedalImage);
                break;

            case BRONZE:
                imageView = new ImageView(bronzeMedalImage);
                break;

            default:
                System.out.println("Bisher keine Medallions");
                break;
        }
        return imageView;
    }

}
