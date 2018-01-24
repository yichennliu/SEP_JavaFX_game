package view;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private TextField playerNameInput;
    private Label giveName;
    private Label boulderdash;
    private String stylesheet;
    private double menuWidth;


    public PrimaryPage(Stage stage,double menuStartWidth, double menuStartHeight){
        stylesheet= fileTolStylesheetString(new File("src/view/style.css"));
        this.stage= stage;
        this.root = new Group();
       // root.setLayoutX(menuStartWidth);
      //  root.setLayoutY(menuStartHeight);
        this.name = new Button(" ok ");
        this.noName = new Button("continue wihout name ");
        this.boulderdash = new Label("B O U L D E R D A S H - W I T C H ");
        this.boulderdash.setId("boulderdash");
        this.giveName = new Label("Enter your Name:");
        this.playerNameInput = new TextField ();

        HBox menuHbox = new HBox(10, name, noName);
        VBox menuVbox = new VBox(boulderdash,giveName,playerNameInput,menuHbox);
        HBox menuGanz= new HBox(menuVbox);
        menuGanz.setAlignment(Pos.BOTTOM_LEFT);
        menuVbox.setAlignment(Pos.BOTTOM_LEFT);




        this.sceneWelcome = new Scene(root);
        sceneWelcome.getStylesheets().add(stylesheet);
       // menuGanz.getChildren().addAll(menuVbox,menuHbox);
        root.getChildren().add(menuGanz);
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
