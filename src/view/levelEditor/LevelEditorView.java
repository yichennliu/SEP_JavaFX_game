package view.levelEditor;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.enums.Token;
import model.levelEditor.LevelEditor;
import view.Board;
import view.View;

public class LevelEditorView {

    private Stage stage;
    private Scene scene;
    private Group root;
    private LevelEditor editor;
    private Board board;
    private BorderPane rootPane;
    private ToggleGroup headerButtons;
    private Canvas staticCanvas, animationCanvas;
    private Group canvasGroup;
    private TextField[] gemInputs;
    private TextField[] timeInputs;
    private double fieldSize = 20.0;
    private Button saveButton;
    private TextField nameInput;

    public LevelEditorView(Stage stage, LevelEditor editor){
        this.editor = editor;
        this.root = new Group();
        this.stage = stage;
        this.scene = new Scene(root);
        initContent();
        if(!stage.isShowing()) stage.show();
    }

    private void initContent(){
        rootPane = new BorderPane();
        root.getChildren().add(rootPane);
        TilePane header = new TilePane();
        header.setPrefColumns(11);
        initHeaderButtons(header);
        initInformationBox();
        createMap();
        rootPane.setTop(header);
        rootPane.setCenter(canvasGroup);
        update();
    }

    private void initHeaderButtons(TilePane header){
        headerButtons = new ToggleGroup();
        for(Token t : Token.values()){
            ToggleButton button = new ToggleButton(t.name());
                header.getChildren().add(button);
            button.setUserData(t);
            button.setToggleGroup(headerButtons);
        }
    }

    private void createMap(){
        staticCanvas = new Canvas(editor.getWidth()*fieldSize,editor.getHeight()*fieldSize);
        animationCanvas = new Canvas();
        animationCanvas.widthProperty().bindBidirectional(staticCanvas.widthProperty());
        animationCanvas.heightProperty().bindBidirectional(staticCanvas.heightProperty());
        this.board = new Board(staticCanvas,animationCanvas,editor.getMap(),editor.getTheme(),fieldSize);
        this.canvasGroup = new Group(animationCanvas,staticCanvas);
    }

    public void update(){
        View.drawBoard(board,editor.getMap(),board.getTheme(),false);
    }

    public Scene getScene() {
        return scene;
    }

    public Canvas getStaticCanvas() {
        return staticCanvas;
    }

    public Board getBoard() {
        return board;
    }

    public ToggleGroup getHeaderButtons() {
        return headerButtons;
    }

    private void initInformationBox(){
        VBox informationRoot = new VBox();
        informationRoot.setSpacing(10.0);
        informationRoot.setStyle("-fx-padding:10");
        nameInput = new TextField();
        nameInput.setPromptText("Levelname");
        Label goalLabel = new Label("Ziele");
        goalLabel.setStyle("-fx-font-weight:bold");
        HBox gemBox = new HBox();
        gemBox.setSpacing(5);
        HBox timeBox = new HBox();
        timeBox.setSpacing(5);
        initGoalsInput();
        gemBox.getChildren().add( new Label("Gems "));
        timeBox.getChildren().add(new Label("Zeit "));

        for(TextField entry: gemInputs){
            gemBox.getChildren().add(entry);
        }

        for(TextField entry: timeInputs){
            timeBox.getChildren().add(entry);
        }

        saveButton = new Button("Speichern");
        informationRoot.getChildren().addAll(nameInput, goalLabel, gemBox, timeBox, saveButton);

        rootPane.setLeft(informationRoot);

    }

    public TextField getNameInput() {
        return nameInput;
    }

    public TextField[] getGemInputs() {
        return gemInputs;
    }

    public TextField[] getTimeInputs() {
        return timeInputs;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    private void initGoalsInput(){
        gemInputs = new TextField[3];
        timeInputs = new TextField[3];
        for (int i = 0; i<gemInputs.length;i++){
            gemInputs[i] = getGemTimeInput(30);
            timeInputs[i] = getGemTimeInput(40);
        }
    }

    private TextField getGemTimeInput(double size){
        TextField inputField = new TextField();
        inputField.setPrefWidth(size);
        return inputField;
    }
}
