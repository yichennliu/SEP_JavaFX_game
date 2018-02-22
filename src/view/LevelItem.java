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
    private Text information;
    private Label name;
    private double width ,height;




    public LevelItem(Label menuItemName, String info, ImageView image, String userData, double width, double height) {
        super(5);
        this.width=width;
        this.height=height;
        this.setUserData(userData);
        this.setCursor(Cursor.HAND);
      //  name = new Label(menuItemName);
        information = new Text(info);
        information.setEffect(new GaussianBlur(1));
  //      name.getStyleClass().add("LabelColor");
        VBox vboxLevelInformation = new VBox(information, menuItemName);
        vboxLevelInformation.getStyleClass().add("vboxlevel");
        BorderPane rightContainer = new BorderPane();
        rightContainer.setRight(image);
        rightContainer.setLeft(vboxLevelInformation);
        image.setFitWidth(150);
        image.setFitHeight(150);
        rightContainer.setMinSize(width/2-100, height/5);
        rightContainer.setMaxSize(width/2-100, height/5);
        rightContainer.getStyleClass().add("backgorungMenuLevel");

        rightContainer.setTranslateX(50);
        getChildren().addAll(rightContainer);


    }


}
