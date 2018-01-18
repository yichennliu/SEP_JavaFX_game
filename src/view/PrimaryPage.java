package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by aidabakhtiari on 16.01.18.
 */
public class PrimaryPage {


    private Scene sceneWelcome;
    private Stage stage;
    private static Group root;
    private static Button name;
    private static Button noName;




    public PrimaryPage(Stage stage){
        this.stage= stage;
        root = new Group();
        VBox menu = new VBox();
        this.name = new Button(" ok ");
        this.noName = new Button("continue wihout name ");


        this.sceneWelcome = new Scene(root);
        menu.getChildren().addAll(name,noName);
        root.getChildren().add(menu);
        if(!stage.isShowing()) stage.show();
    }

    public static Button getName() {
        return name;
    }

    public static Button getNoName() {
        return noName;

    }


    public Scene getScene() {
        return this.sceneWelcome;
    }
}
