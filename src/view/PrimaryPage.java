package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by aidabakhtiari on 16.01.18.
 */
public class PrimaryPage {


    private Scene sceneWelcome;
    private Stage stage;
    private static Group root;
    private Button name;
    private Button noName;
    private  TextField playerNameInput;
    private String stylesheet;



    public PrimaryPage(Stage stage){
        this.stage= stage;
        root = new Group();

        this.name = new Button(" ok ");
        this.noName = new Button("continue wihout name ");
        Label giveName = new Label("please give me your Name:");
        HBox menuHbox = new HBox(10, name, noName);

        VBox menuVbox = new VBox(10);

         playerNameInput = new TextField ();
         stylesheet= fileTolStylesheetString(new File("/Users/aidabakhtiari/Desktop/YAMAI_HAUPTPROJEKT/src/view/style.css"));


        this.sceneWelcome = new Scene(root);
        sceneWelcome.getStylesheets().add(stylesheet);

        menuVbox.getChildren().addAll(giveName,playerNameInput,menuHbox);
        root.getChildren().add(menuVbox);
        if(!stage.isShowing()) stage.show();


    }





    public static String fileTolStylesheetString(File stylesheet){
        try {
            return stylesheet.toURI().toURL().toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }


    }

    public String getPlayerName(){
        return  playerNameInput.getText();
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
