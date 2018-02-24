package view;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Created by aidabakhtiari on 22.02.18.
 */
public class LevelItem extends HBox {

    private View view;
    private Label name, information,gems;
    private HBox medalBox;
    private double width ,height;


    public LevelItem(String menuItemName, String info, String gemsInfo, ImageView image, String userData, double width, double height, List<ImageView> medals) {
        super(5);
        this.setUserData(userData);
        this.setCursor(Cursor.HAND);
        name = new Label(menuItemName);
        gems = new Label(gemsInfo);
        gems.setTranslateX(50);
        gems.setTranslateY(10);
        information = new Label(info);
        information.setEffect(new GaussianBlur(1));
        medalBox = new HBox();
        medalBox.setSpacing(10);
        for (ImageView medal: medals) {
            medal.setFitHeight(25);
            medal.setFitWidth(25);
            medalBox.getChildren().add(medal);
        }
        HBox levelInformation = new HBox(name, medalBox);
        levelInformation.setSpacing(10);
        BorderPane rootBox = new BorderPane();
        rootBox.setTop(levelInformation);
        HBox leftBox = new HBox(information, gems);
        rootBox.setLeft(leftBox);
        image.setFitWidth(150);
        image.setFitHeight(150);
        rootBox.setRight(image);
        rootBox.setMinSize(width, height);
        rootBox.setMaxSize(width, height);
        rootBox.getStyleClass().add("backgroundcolorBlue");
        levelInformation.getStyleClass().add("vboxlevel");
        information.getStyleClass().add("infoOnLevelItem");
        name.getStyleClass().add("infoOnLevelItemBig");
        gems.getStyleClass().addAll("infoOnLevelItemSmall");

        rootBox.setTranslateX(50);
        getChildren().addAll(rootBox);
    }


}
