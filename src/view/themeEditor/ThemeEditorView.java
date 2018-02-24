package view.themeEditor;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.enums.Token;
import model.themeEditor.Theme;
import model.themeEditor.Theme.FeldType;
import view.MenuView;

import java.io.File;
import java.util.Arrays;

public class ThemeEditorView {
private Theme theme;
private Group root;
private Stage stage;
private Scene sceneThemeView;
private Canvas themeCanvas;
private GraphicsContext themeGC;
private TreeView<view.themeEditor.Cell> treeView;
private ImageView preview;
private ToggleGroup positionButtonGroup;
private Button nextFrame;
private Button previousFrame;
private TextField frameNumberField;
private BorderPane rootPane;
private VBox positionButtons;
private GridPane previewGridPane;
private TextField frameCount;
private TextField spriteSize;
private TextField fptField;
private Button exportButton;
private Button exitButton;
private TextField nameInput;
private Group ioButtons;
private VBox header;
private MenuView menuView;
private double width,height;


ComboBox<String> themeChoiceBox;

private ObservableList<String> themes;
private String stylesheet;

    public ThemeEditorView(Stage stage){

        root = new Group();
        HBox background = new HBox(root);
        this.sceneThemeView = new Scene(background);
        this.stage = stage;
        this.width=stage.getWidth();
        this.height=stage.getHeight();
        rootPane = new BorderPane();
        rootPane.setPrefSize(width/3,height-100);
        background.setPrefSize(width,height);
        background.getStyleClass().add("backgroundcolorBlue");
        background.setAlignment(Pos.CENTER);
        /* TreeView */
        TreeItem<view.themeEditor.Cell> treeRoot = new TreeItem<view.themeEditor.Cell> (null);
        treeRoot.setExpanded(true);

        for(Token t : Token.values()){
            TreeItem<view.themeEditor.Cell> tokenItem = new TreeItem<view.themeEditor.Cell>(new view.themeEditor.Cell(t));
            for(FeldType f : FeldType.values()){
                if(f.isMovable()==t.isMovable()){
                    TreeItem<view.themeEditor.Cell> feldTypeItem = new TreeItem<view.themeEditor.Cell>(new view.themeEditor.Cell(t,f));
                    tokenItem.getChildren().add(feldTypeItem);
                }
            }
            treeRoot.getChildren().add(tokenItem);
        }

        treeView = new TreeView<view.themeEditor.Cell>(treeRoot);
        treeView.setShowRoot(false);

        /* Positioncontainer */
        initPositionPaneRoot();
        initHeader();
        initIOButtons();
        initExitButton();
        rootPane.setLeft(treeView);
        rootPane.setTranslateX(width/4);
        rootPane.setTranslateY(50);


        stylesheet = menuView.fileToStylesheetString(new File("src/view/style.css"));
        sceneThemeView.getStylesheets().add(stylesheet);

        root.getChildren().add(rootPane);
        if(!stage.isShowing()) stage.show();
    }

    private void initHeader(){
        header = new VBox();
        initThemeSelection();
        rootPane.setBottom(header);
    }

    private void initThemeSelection(){
        updateThemeList();
        themeChoiceBox = new ComboBox<>();
        themeChoiceBox.setPromptText("Lade Theme");
        themeChoiceBox.setItems(this.themes);

        this.nameInput = new TextField();
        nameInput.setPrefColumnCount(10);
        nameInput.setPromptText("[Theme-Name]");
        this.header.getChildren().addAll(themeChoiceBox,nameInput);
    }

    public TextField getNameInput() {
        return nameInput;
    }

    public ComboBox<String> getThemeChoiceBox() {
        return themeChoiceBox;
    }

    public void updateThemeList(){
        File themeDir = new File("src/json/theme");
        if(themeDir.exists()){
            this.themes = FXCollections.observableArrayList(Arrays.asList(themeDir.list()));
            if(themeChoiceBox!=null) themeChoiceBox.setItems(themes);
        }
    }

    private void initIOButtons(){
        this.exportButton = new Button("Speichern");
        exportButton.setGraphic(new ImageView("view/themeEditor/save.png"));
        ioButtons = new Group();
        ioButtons.getChildren().addAll(exportButton);
        this.header.getChildren().add(exportButton);
    }

    private void initExitButton(){
        this.exitButton = new Button("Zurück zum Menü");
        this.header.getChildren().add(0,exitButton);
    }

    private void initPositionPaneRoot(){
        double maxWidth =width;

        GridPane positionPaneRoot = new GridPane();
        positionPaneRoot.setGridLinesVisible(true);
        GridPane buttonGridPane = new GridPane();
        this.previewGridPane = new GridPane();

        buttonGridPane.setMinWidth(width/3);
        buttonGridPane.setMinSize(width/3,height/2);
        previewGridPane.setDisable(true);

        this.nextFrame = new Button(); nextFrame.setGraphic(new ImageView("model/themeEditor/thumbnails/next.png"));
        this.previousFrame = new Button(); previousFrame.setGraphic(new ImageView("model/themeEditor/thumbnails/previous.png"));
        this.frameNumberField = new TextField("0");
        frameNumberField.setMaxWidth(30);
        frameNumberField.setMinWidth(30);
        frameNumberField.setEditable(false);
        TilePane frameControllers = new TilePane();
        frameControllers.setAlignment(Pos.CENTER);
        frameControllers.setMaxWidth(maxWidth);
        frameControllers.getChildren().addAll(previousFrame,frameNumberField,nextFrame);

        frameCount = new TextField();
        spriteSize = new TextField();
        fptField = new TextField();
        TilePane sizeAndCountPane = new TilePane();
        sizeAndCountPane.getChildren().addAll(new Label("Frames: "),frameCount, new Label("SpriteSize: "),spriteSize, new Label("Frames per tick: "),fptField);
        sizeAndCountPane.setMaxWidth(maxWidth);
        sizeAndCountPane.setAlignment(Pos.CENTER);

        frameCount.setMaxWidth(30);
        spriteSize.setMaxWidth(30);
        fptField.setMaxWidth(30);

        positionButtonGroup = new ToggleGroup();
        positionButtons = new VBox();

        preview = new ImageView("model/themeEditor/thumbnails/defaultPreview.png");
        preview.setPreserveRatio(true);
        preview.setFitHeight(100);
        preview.setDisable(true);
        ImageView previewBackground = new ImageView(new Image("model/themeEditor/thumbnails/previewBackground.png"));
        previewBackground.setPreserveRatio(true);
        previewBackground.setFitHeight(100);
        Group previewGroup = new Group();
        previewGroup.getChildren().addAll(previewBackground,preview);

        TilePane previewTilePane = new TilePane();previewTilePane.setMaxWidth(maxWidth);
        previewTilePane.setAlignment(Pos.CENTER);
        previewTilePane.getChildren().add(previewGroup);

        previewGridPane.add(frameControllers,0,0);
        previewGridPane.add(previewTilePane,0,1);
        previewGridPane.add(sizeAndCountPane,0,2);
        buttonGridPane.add(positionButtons,0,1);

        positionPaneRoot.add(buttonGridPane,0,0);
        positionPaneRoot.add(previewGridPane,1,0);
        rootPane.setCenter(positionPaneRoot);
    }


    public Scene getScene(){
        return this.sceneThemeView;
    }

    public ReadOnlyObjectProperty<TreeItem<Cell>> getSelectedItemProperty() {
        return treeView.getSelectionModel().selectedItemProperty();
    }

    public VBox getPositionButtons(){
        return this.positionButtons;
    }

    public ImageView getPreview() {
        return preview;
    }

    public GridPane getPreviewGridPane(){
        return this.previewGridPane;
    }

    public ToggleGroup getPositionButtonGroup() {
        return positionButtonGroup;
    }

    public Button getNextFrame() {
        return nextFrame;
    }

    public Button getPreviousFrame() {
        return previousFrame;
    }

    public TextField getFrameCount() {
        return frameCount;
    }

    public TextField getSpriteSize() {
        return spriteSize;
    }

    public TextField getFrameNumberField() {
        return frameNumberField;
    }

    public void setTheme(Theme theme){
        this.theme = theme;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public TextField getFramesPerSecondField() {
        return fptField;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
