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
    private  RadioButton level1;
    private  RadioButton level2;
    private  RadioButton level3;
    private ToggleGroup group;
    private static Group root;
    private String  stylesheet;


    public MenuView(Stage stage, Object model, String playerName){
        this.stage= stage;
         root = new Group();
        VBox menu = new VBox(10);

        this.gameButton = new Button(" s t a r t  G A M E ");
        this.themeEditorButton = new Button("s t a r t  T H E M E - E D I T O R");
        this.helpButton = new Button ("H E L P");
        this.showName= new Label("W I L L K O M M E N " + playerName);
        level1 = new RadioButton("kasdhkasjdh");
        level2 = new RadioButton("asjdlaskdj");
        level3 = new RadioButton("asödlkaösldk");
        group = new ToggleGroup();
        level1.setToggleGroup(group);
        level2.setToggleGroup(group);
        level3.setToggleGroup(group);
        level1.setSelected(true);
        level1.setUserData("Home");
        level2.setUserData("Calendar");
        level3.setUserData("Contacts");
        HBox menuHbox = new HBox(10,level1,level2,level3);
        this.sceneMenu = new Scene(root);
        menu.getChildren().addAll(showName,gameButton,menuHbox,helpButton,themeEditorButton);
        root.getChildren().add(menu);
        if(!stage.isShowing()) stage.show();
        stylesheet= PrimaryPage.fileTolStylesheetString(new File("/Users/aidabakhtiari/Desktop/YAMAI_HAUPTPROJEKT/src/view/style.css"));
        sceneMenu.getStylesheets().add(stylesheet);


 /*
        ImageView imageView = new ImageView("spritetokens_nongif.png");
        final Image image = new Image(getClass().getResourceAsStream(group.getSelectedToggle().getUserData().toString() +
                ".png"));
        imageView.setImage(image);

      group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (group.getSelectedToggle()!=null){
                    final Image image = new Image(getClass().getResourceAsStream(group.getSelectedToggle().getUserData().toString() +
                            ".jpg"));


                }
            }
        });
        imageView.setImage(image);

*/

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


