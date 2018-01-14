package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;



public class Menu {

    private Scene scene;
    private Button gameButton;

    public Menu(Stage stage){
        Group root = new Group();
        this.scene = new Scene(root);
    }


    public Button getGameButton(){
        return this.gameButton;
    }

    public Scene getScene() {
        return this.scene;
    }
}
