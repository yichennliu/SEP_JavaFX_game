package view;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Created by aidabakhtiari on 22.02.18.
 */
public class LevelItem extends HBox {

    private View view;
    private Label name,information;
    private double width ,height;




    public LevelItem(String menuItemName, String info, ImageView image, String userData, double width, double height) {
        super(5);
        this.width=width;
        this.height=height;
        this.setUserData(userData);
        this.setCursor(Cursor.HAND);
        name = new Label(menuItemName);
        information = new Label(info);
        information.setEffect(new GaussianBlur(1));
        VBox vboxLevelInformation = new VBox(name);
        BorderPane rightContainer = new BorderPane();
        rightContainer.setRight(image);
        rightContainer.setBottom(information);
        rightContainer.setLeft(vboxLevelInformation);
        image.setFitWidth(150);
        image.setFitHeight(150);
        rightContainer.setMinSize(width, height);
        rightContainer.setMaxSize(width, height);
        rightContainer.getStyleClass().add("backgorungMenuLevel");
        vboxLevelInformation.getStyleClass().add("vboxlevel");
        information.getStyleClass().add("infoOnLevelItem");
        name.getStyleClass().add("infoOnLevelItem");

        rightContainer.setTranslateX(50);
        getChildren().addAll(rightContainer);


    }


}
