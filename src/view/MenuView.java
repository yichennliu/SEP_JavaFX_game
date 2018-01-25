package view;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.File;



public class MenuView {

    private Scene sceneMenu,sceneHelp;
    private Button gameButton;
    private Button helpButton;
    private Button themeEditorButton;
    private Label showName;
    private Stage stage;
    private  ToggleButton level1;
    private  ToggleButton level2;
    private  ToggleButton level3;
    private ToggleGroup group;
    private static Group root;
    private String  stylesheet;


    public MenuView(Stage stage, Object model, String playerName) {
        this.stage = stage;
        root = new Group();
        VBox menu = new VBox(10);

        this.gameButton = new Button(" s t a r t  G A M E ");
        this.themeEditorButton = new Button("s t a r t  T H E M E - E D I T O R");
        this.helpButton = new Button("H E L P");
        this.showName = new Label("W I L L K O M M E N " + playerName);
        Image levelImage1 = new Image(getClass().getResourceAsStream("level1.png"));
        Image levelImage2 = new Image(getClass().getResourceAsStream("level2.gif"));

        gameButton.getTypeSelector();

        level1 = new  ToggleButton("level : WALD", new ImageView(levelImage1));
        level2 = new  ToggleButton(" : ",new ImageView(levelImage2));
        level3 = new  ToggleButton("asöösldk");
        group = new ToggleGroup();
        level1.setToggleGroup(group);
        level2.setToggleGroup(group);
        level3.setToggleGroup(group);

        level1.setUserData("Home");
        level2.setUserData("Calendar");
        level3.setUserData("Contacts");
        HBox menuHbox = new HBox(10, level1, level2, level3);
        this.sceneMenu = new Scene(root);
        menu.getChildren().addAll(showName, gameButton, menuHbox, helpButton, themeEditorButton);
        root.getChildren().add(menu);
        if (!stage.isShowing()) stage.show();
        stylesheet = PrimaryPage.fileTolStylesheetString(new File("src/view/style.css"));
        sceneMenu.getStylesheets().add(stylesheet);


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


   public ToggleGroup getRadiobuttons(){
        return group;
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


