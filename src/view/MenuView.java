package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MenuView {

    private Scene sceneMenu, sceneHelp;
    private Stage stage;
    private ToggleGroup group;
    private String stylesheet;
    private List<ToggleButton> levelButtons;
    private BorderPane root;
    private  ContentFrame contentFrame;

    public MenuView(Stage stage, Object model) {

        this.stage = stage;

        root = new BorderPane();


        contentFrame = new ContentFrame();

        VBox Vmenu= new VBox(contentFrame);
        this.levelButtons = new ArrayList<>();
        this.sceneMenu = new Scene(root);
        root.getChildren().addAll(Vmenu);

        if (!stage.isShowing()) stage.show();
       stylesheet = PrimaryPage.fileToStylesheetString(new File("src/view/style.css"));
       sceneMenu.getStylesheets().add(stylesheet);



    }

    private String[] scanLevelDirectory() {
        File dir = new File("src/json/level");
        return dir.list();
    }



    public Scene getSceneMenu() {
        return this.sceneMenu;
    }


    public ContentFrame getContentFrame() {
        return contentFrame;
    }




}


