package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.game.MedalStatus;
import view.menuview.ContentFrame;

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
        this.width= stage.getWidth();
        this.height= stage.getHeight();
        this.medalStatuses = medalStatuses;
        contentFrame = new ContentFrame(width,height,this);
        VBox vMenu= new VBox(contentFrame);
        this.levelButtons = new ArrayList<>();
        root.getChildren().addAll(vMenu);
        if (!stage.isShowing()) stage.show();
        this.stylesheet = fileToStylesheetString(new File("src/view/style.css"));
        sceneMenu.getStylesheets().add(stylesheet);
        System.out.println("Neue MEnuView, Größe der Map: " + medalStatuses.size() + " " + medalStatuses);
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}


