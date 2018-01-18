package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by aidabakhtiari on 16.01.18.
 */
public class PrimaryPage {


    private Scene sceneWelcome;
    private Stage stage;
    private static Group root;
    private Button name;
    private Button noName;
    private String playerName;
    private  static TextField playerNameInput;


    public PrimaryPage(Stage stage){
        this.stage= stage;
        root = new Group();
        VBox menu = new VBox();
        this.name = new Button(" ok ");
        this.noName = new Button("continue wihout name ");
        Label giveName = new Label("please give me your Name:");
         playerNameInput = new TextField ();

        this.sceneWelcome = new Scene(root);
        menu.getChildren().addAll(giveName,playerNameInput,name,noName);
        root.getChildren().add(menu);
        if(!stage.isShowing()) stage.show();
    }




    public TextField getPlayerName(){
        return  playerNameInput;
    }

    public Button getName() {
        return name;
    }

    public Button getNoName() {
        return noName;

    }


    public Scene getScene() {
        return this.sceneWelcome;
    }
}
