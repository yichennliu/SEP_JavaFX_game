package view;

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
import javafx.stage.Stage;
import model.enums.Token;
import model.themeEditor.Theme;
import model.themeEditor.Theme.FeldType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ThemeEditorView {
private Theme theme;
private Group root;
private Stage stage;
private Scene sceneThemeView;
private Canvas themeCanvas;
private GraphicsContext themeGC;
private TreeView<Cell> treeView; // + selected ItemProperty
private ImageView preview; // +
private ToggleGroup positionButtonGroup;  // + positionButtonGroup.selectedToggleProperty()
private Button nextFrame; //+
private Button previousFrame; //+
private TextField frameNumberField; //+
private BorderPane rootPane;
private VBox positionButtons;
private GridPane previewGridPane;
private TextField frameCount;
private TextField spriteSize;
private TextField fptField;
private Button exportButton;
private Button importButton;
private VBox ioButtons;
private HBox header;

private ObservableList<String> themes;

private String stylesheet;

    public ThemeEditorView(Stage stage){
        root = new Group();
        this.sceneThemeView = new Scene(root);
        this.stage = stage;

        rootPane = new BorderPane();


        /* TreeView */
        TreeItem<Cell> treeRoot = new TreeItem<Cell> (null);
        treeRoot.setExpanded(true);

        for(Token t : Token.values()){
            TreeItem<Cell> tokenItem = new TreeItem<Cell>(new Cell(t));
            for(FeldType f : FeldType.values()){
                if(f.isMovable()==t.isMovable()){
                    TreeItem<Cell> feldTypeItem = new TreeItem<Cell>(new Cell(t,f));
                    tokenItem.getChildren().add(feldTypeItem);
                }
            }
            treeRoot.getChildren().add(tokenItem);
        }

        treeView = new TreeView<Cell>(treeRoot);
        treeView.setShowRoot(false);

        /* Positioncontainer */
        initPositionPaneRoot();
        initOIButtons();
        initHeader();
        rootPane.setLeft(treeView);

        root.getChildren().add(rootPane);
        if(!stage.isShowing()) stage.show();
    }

    private void initHeader(){
        header = new HBox();
        initThemeSelection();
        rootPane.setTop(header);
    }

    private void initThemeSelection(){
        updateThemeList();
        ComboBox<String> themeChoiceBox = new ComboBox<>();
        themeChoiceBox.setPromptText("Lade Theme");
        themeChoiceBox.setItems(this.themes);
        this.header.getChildren().add(themeChoiceBox);

    }

    private void updateThemeList(){
        File themeDir = new File("src/json/theme");
        if(themeDir.exists()){
            this.themes = FXCollections.observableArrayList(Arrays.asList(themeDir.list()));
        }
    }

    private void initOIButtons(){
        this.exportButton = new Button("Überschreibe TestTheme");
        this.importButton = new Button("Lade TestTheme");
        ioButtons = new VBox();
        ioButtons.getChildren().addAll(exportButton,importButton);
        this.rootPane.setRight(ioButtons);
    }


    private void initPositionPaneRoot(){
        double maxWidth = 150;

        GridPane positionPaneRoot = new GridPane();
        positionPaneRoot.setGridLinesVisible(true);
        GridPane buttonGridPane = new GridPane();
        this.previewGridPane = new GridPane();

        buttonGridPane.setMinWidth(150);
        previewGridPane.setDisable(true);

        this.nextFrame = new Button(); nextFrame.setGraphic(new ImageView("model/themeEditor/thumbnails/next.png"));
        this.previousFrame = new Button(); previousFrame.setGraphic(new ImageView("model/themeEditor/thumbnails/previous.png"));
        this.frameNumberField = new TextField("0");
        frameNumberField.setMaxWidth(30);
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

    public Button getImportButton() {
        return importButton;
    }

    public TextField getFramesPerSecondField() {
        return fptField;
    }
}
