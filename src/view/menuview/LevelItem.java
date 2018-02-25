package view.menuview;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import view.View;

import java.util.List;

/**
 * Created by aidabakhtiari on 22.02.18.
 */
public class LevelItem extends HBox {

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
        rootBox.setBottom(new Label(" "));
        rootBox.getStyleClass().add("backgroundcolorBlue");
        levelInformation.getStyleClass().add("vboxlevel");
        information.getStyleClass().add("infoOnLevelItem");
        gems.getStyleClass().add("infoOnLevelItem");
        name.getStyleClass().add("infoOnLevelItemBig");
        gems.getStyleClass().addAll("infoOnLevelItemSmall");

        rootBox.setTranslateX(50);
        getChildren().addAll(rootBox);
    }


    public LevelItem(String menuItemName, ImageView image, double width, double height) {
        super(5);
        this.width=width;
        this.height=height;

        name = new Label(menuItemName);

        BorderPane rightContainer = new BorderPane();
        rightContainer.setRight(image);
        rightContainer.setLeft(name);
        image.setFitWidth(100);
        image.setFitHeight(100);
        rightContainer.setMinSize(width, height);
        rightContainer.setMaxSize(width, height);

        rightContainer.getStyleClass().add("backgroundcolorBlue");
        name.getStyleClass().add("infoOnLevelItemBig");

        rightContainer.setTranslateX(50);
        getChildren().addAll(rightContainer);


    }


}
