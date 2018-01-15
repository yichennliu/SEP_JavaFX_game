package view;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import main.LevelImporter;
import model.Level;


public class Menu {

    private Scene sceneMenu,sceneHelp;
    private Button gameButton;
    private Button helpButton;
    private Stage stage;
    private static Group root;
    private int width;
    private int height;

    public Menu(Stage stage){
        this.stage= stage;
         root = new Group();
       // VBox layoutMenu = new VBox(gameButton,helpButton);
       // BorderPane borderMenu = new BorderPane(layoutMenu);
        //borderMenu.setAlignment(layoutMenu, Pos.CENTER);
     //   layoutMenu.setAlignment(Pos.CENTER);


        this.gameButton = new Button(" s t a r t  G A M E ");

        this.helpButton = new Button ("H E L P");

        this.sceneMenu = new Scene(root);
        setGameButton();
        setHelpButton();
        root.getChildren().addAll(gameButton,helpButton);

    }

    public void setGameButton() {
        gameButton.setOnAction((ActionEvent event) -> {
            Level level = LevelImporter.importLevel("json/text.json") ;
            View view = new View(level,stage);
            stage.centerOnScreen();
            view.update(View.Mode.GAME);

        });



    }



    public void setHelpButton() {
        helpButton.setOnAction((ActionEvent event) -> {
            Dialog<Pair<String, String>> helpWindow = new Dialog<>();
            helpWindow.setTitle("HELP INSTRUCTIONS");
            helpWindow.setHeaderText("DIESER TEXT  IST EIN DUMMIE : ");
            helpWindow.setContentText(" hier findest du Hilfe : http://code.makery.ch/blog/javafx-dialogs-official/");
            helpWindow.initStyle(StageStyle.UTILITY);
            ButtonType okButton = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
            helpWindow.getDialogPane().getButtonTypes().addAll(okButton);
              helpWindow.showAndWait();

        });
    }






    public Button getGameButton(){

        return this.gameButton;
    }

    public Scene getSceneMenu() {
        return this.sceneMenu;
    }
}
