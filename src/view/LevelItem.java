package view;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Created by aidabakhtiari on 22.02.18.
 */
public class LevelItem extends HBox {
    private Button levelButton;
    private Text information;
    private Runnable script;


    public LevelItem(String menuItemName, String info, ImageView image, String userData) {
        super(5);
        this.setUserData(userData);
        this.setCursor(Cursor.HAND);
        Label levelname = new Label(menuItemName);
        information = new Text(info);
        information.setEffect(new GaussianBlur(1));
        levelname.getStyleClass().add("textColor");
        VBox vboxLevelInformation = new VBox(information, levelname);
        vboxLevelInformation.setAlignment(Pos.CENTER_LEFT);
        HBox levelsContainer = new HBox(vboxLevelInformation, image);
        levelsContainer.setMinSize(800, 200);
        levelsContainer.getStyleClass().add("backgorungMenuLevel");
        levelsContainer.setTranslateX(50);
        getChildren().addAll(levelsContainer);


    }


}
