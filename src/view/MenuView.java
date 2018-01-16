package view;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;


public class MenuView {

    private Scene sceneMenu,sceneHelp;
    private Button gameButton;
    private Button helpButton;
    private Button themeEditorButton;

    private Stage stage;
    private static Group root;
    private int width;
    private int height;

    public MenuView(Stage stage, Object model){
        this.stage= stage;
         root = new Group();
        VBox menu = new VBox();
        this.gameButton = new Button(" s t a r t  G A M E ");
        this.themeEditorButton = new Button("s t a r t  T H E M E - E D I T O R");

        this.helpButton = new Button ("H E L P");

        this.sceneMenu = new Scene(root);
        menu.getChildren().addAll(gameButton,helpButton,themeEditorButton);
        root.getChildren().add(menu);
        if(!stage.isShowing()) stage.show();
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

    public Button getThemeEditorButton(){
        return this.themeEditorButton;
    }

    public Scene getSceneMenu() {
        return this.sceneMenu;
    }
}
