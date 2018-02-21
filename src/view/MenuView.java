package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.game.MedalStatus;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuView {
    private Scene sceneMenu, sceneHelp;
    private Stage stage;
    private Map<String, MedalStatus> medalStatuses;
    private ToggleGroup group;
    private String stylesheet;
    private List<ToggleButton> levelButtons;
    private BorderPane root;
    private ContentFrame contentFrame;
    private double width;
    private double height;

    public MenuView(Stage stage, Map <String, MedalStatus> medalStatuses) {
        this.stage = stage;
        root = new BorderPane();
        this.sceneMenu = new Scene(root);
         width= stage.getWidth();
         height= stage.getHeight();
        this.medalStatuses = medalStatuses;
        contentFrame = new ContentFrame(width,height,this);
        VBox Vmenu= new VBox(contentFrame);
        this.levelButtons = new ArrayList<>();
        //this.sceneMenu = new Scene(root);
        root.getChildren().addAll(Vmenu);
        if (!stage.isShowing()) stage.show();
        stylesheet = fileToStylesheetString(new File("src/view/style.css"));
        sceneMenu.getStylesheets().add(stylesheet);
        root.getStyleClass().add("borderPane");


    }

    public Scene getSceneMenu() {
        return this.sceneMenu;
    }


    public ContentFrame getContentFrame() {
        return contentFrame;
    }

    public Map<String, MedalStatus> getMedalStatuses() {
        return this.medalStatuses;
    }

    public static String fileToStylesheetString(File stylesheet) {
        try {
            return stylesheet.toURI().toURL().toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }


    }






}


