package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.game.Feld;
import model.enums.Token;
import model.themeEditor.ThemeEditor;

import java.util.HashMap;
import java.util.Map;

public class ThemeEditorView {

private ThemeEditor themeEditor;
private Theme theme;
private Group root;
private Stage stage;
private Scene scene;
private Canvas themeCanvas;
private GraphicsContext themeGC;
private Feld[][] testFeld;
private TabPane tabs;
private Map<Token,TabPane> typeTabs;

private GridPane tabContentAndCanvas;
private Button addButton;
private Button removeButton;
    private Button backButton;
private ImageView preview;
private Label label;
private ListView listView;


    public ThemeEditorView(Stage stage, ThemeEditor themeEditor){
        root = new Group();

        this.testFeld = new Feld[5][6];
        this.theme = new Theme();
        this.typeTabs = new HashMap<Token,TabPane>();

        this.scene = new Scene(this.root);
        this.stage = stage;
        this.themeEditor = themeEditor;
        double width = stage.getWidth();
        double height = stage.getHeight();

        this.tabs = new TabPane();
        this.tabs.setStyle("-fx-font-size: 11;");
        this.tabs.setMaxWidth(width-10);

        initTabContent();

       /* create Tabs */
        for(Token t: Token.values()){
            /* create Tab-Content (type-tabs) */
            HBox tabContent = new HBox();
            tabContent.setStyle("-fx-padding: 2px; ");
            TabPane typeTabPane = new TabPane();

            // create Tabs for every feldtype
            for(Theme.FeldType feldType: Theme.FeldType.values()){
                Tab typeTab = new Tab();
                typeTab.setClosable(false);
                typeTab.setText(feldType.name());
                typeTabPane.getTabs().add(typeTab);
            }
            tabContent.getChildren().add(typeTabPane);
            this.typeTabs.put(t,typeTabPane);
            Tab tab = new Tab();
            tab.setText(t.name());
            tab.setContent(tabContent);
            tab.setClosable(false);
            tabs.getTabs().add(tab);
            if(!stage.isShowing()) stage.show();
        }

        initCanvas();

        VBox rootBox = new VBox();
        rootBox.getChildren().add(this.tabs);
        rootBox.getChildren().add(this.tabContentAndCanvas);

        root.getChildren().addAll(rootBox);
    }

    private Image getTypeThumbnail(Theme.FeldType feldType){
       String fileName = feldType.name().toLowerCase();
        return new Image("view/type_thumbnails/"+fileName+".png");

    }

    private void initTabContent(){
        this.addButton = new Button("Bild hinzufügen");
        this.removeButton = new Button("Bild löschen");
        this.backButton = new Button("zurück zu Menu");
        this.preview = new ImageView();
        this.listView = new ListView();
        this.label = new Label();

        tabContentAndCanvas = new GridPane();
        GridPane tabContent = new GridPane();
        HBox buttons = new HBox();

        tabContent.setStyle("-fx-padding:4px");
        this.listView.setPlaceholder(this.label);

        this.listView.setPrefHeight(50);

        this.preview.setPreserveRatio(true);
        this.preview.setFitHeight(50);
        this.removeButton.setDisable(true);

        buttons.getChildren().addAll(this.addButton,this.removeButton,this.backButton);

        tabContent.add(label,0,0);
        tabContent.add(listView,0,1);
        tabContent.add(buttons,0,2);
        tabContent.add(preview,1,1);

        initCanvas();

        tabContentAndCanvas.add(tabContent,0,0);
        tabContentAndCanvas.add(this.themeCanvas,1,0);



    }
    private void initCanvas(){
        themeCanvas = new Canvas();
        themeCanvas.setHeight(200);
        themeCanvas.setWidth(200);
        themeGC = themeCanvas.getGraphicsContext2D();
        themeGC.fillRect(0,0,themeCanvas.getWidth(),themeCanvas.getHeight());

    }

    public GraphicsContext getGraphicsContext(){
        return this.themeGC;
    }

    public Token getActiveToken(){
        int index = this.tabs.getSelectionModel().getSelectedIndex();
        return Token.values()[index];
    }

    public Theme.FeldType getActiveFeldType(){
        Token activeToken = getActiveToken();
        int index = this.typeTabs.get(activeToken).getSelectionModel().getSelectedIndex();
      return Theme.FeldType.values()[index];
    }

    public Button getAddButton(){
        return this.addButton;
    }

    public Button getRemoveButton(){
        return this.removeButton;
    }


   public Button getBackButton(){ return this.backButton;}

    public ImageView getPreview(){
        return this.preview;
    }

    public ListView getListView(){
        return this.listView;
    }

    public Scene getScene(){
        return this.scene;
    }

    public Label getLabel() {
        return this.label;
    }

    public Map<Token,TabPane> getTypeTabs(){
        return this.typeTabs;
    }

    public TabPane getTabs(){
        return this.tabs;
    }
}
