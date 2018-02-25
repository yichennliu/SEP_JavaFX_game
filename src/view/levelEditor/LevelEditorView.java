package view.levelEditor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.LevelFactory;
import model.enums.Property;
import model.enums.Token;
import model.game.Level;
import model.levelEditor.LevelEditor;
import view.Board;
import view.GamePreview;
import view.View;
import view.MenuView;

import java.io.File;
import java.util.Map;

public class LevelEditorView {

    private Stage stage;
    private Scene scene;
    private Group root;
    private LevelEditor editor;
    private Board board;
    private BorderPane rootPane;
    private ToggleGroup headerButtons;
    private Canvas staticCanvas, animationCanvas, selectionCanvas;
    private Group canvasGroup;
    private TextField difficultInput;
    private TextField[] gemInputs;
    private TextField[] timeInputs;
    private double fieldSize = 20.0;
    private Button saveButton;
    private Button exitButton;
    private ComboBox<String> loadBox;
    private TextField nameInput;
    private TableView<Map.Entry<Property,Integer>> tableView;
    private ToggleGroup modeButtons;
    private  Button addPropertyButton;
    private TextField propertyValueInput;
    private ComboBox<Property> selectPropertyBox;
    private TextField colInput;
    private TextField rowInput;
    private Button cropButton;
    private MenuView menuView;
    private  String stylesheet;
    private TextField diffField;

    public LevelEditorView(Stage stage, LevelEditor editor){
        this.editor = editor;
        this.root = new Group();
        this.stage = stage;
        this.scene = new Scene(root);
        stylesheet = menuView.fileToStylesheetString(new File("src/view/style.css"));
        scene.getStylesheets().add(stylesheet);
        initContent();
        if(!stage.isShowing()) stage.show();
    }

    private void initContent(){
        rootPane = new BorderPane();
        root.getChildren().add(rootPane);
        TilePane header = new TilePane();
        header.setPrefColumns(12);
        initHeaderButtons(header);
        initInformationBox();
        initPropertyBox();
        createMap();
        rootPane.setTop(header);
        rootPane.setCenter(canvasGroup);
        rootPane.setMinSize(stage.getWidth(),stage.getHeight());
        rootPane.setAlignment(canvasGroup, Pos.CENTER);
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
            modeButtons = new ToggleGroup();
            HBox buttonBox = new HBox();
            ToggleButton selectMode = new ToggleButton();
            selectMode.setGraphic(new ImageView(new Image("/view/levelEditor/select.png")));
            ToggleButton brushMode = new ToggleButton();
            brushMode.setGraphic(new ImageView(new Image("/view/levelEditor/brush.png")));
            brushMode.setUserData(LevelEditor.Mode.BRUSH);
            buttonBox.getChildren().addAll(selectMode,brushMode);
            modeButtons.getToggles().addAll(selectMode,brushMode);
            modeButtons.setUserData(LevelEditor.Mode.SELECT);
            modeButtons.selectToggle(brushMode);
            header.getChildren().add(buttonBox);
    }

    private HBox createAndGetCropInputs(){
        HBox cropInputRoot = new HBox();
        colInput = new TextField();
        rowInput = new TextField();
        cropButton = new Button();
        colInput.setPrefColumnCount(3);
        rowInput.setPrefColumnCount(3);
        colInput.setPromptText("Breite");
        rowInput.setPromptText("Höhe");
        Text x = new Text("x");
        x.setStyle("-fx-font-weight:bold");
        cropButton.setGraphic(new ImageView(new Image("/view/levelEditor/crop.png")));
        cropInputRoot.getChildren().addAll(cropButton,colInput,x,rowInput);
        cropInputRoot.setSpacing(5.0);
        return cropInputRoot;
    }

    private void createMap(){
        staticCanvas = new Canvas(editor.getWidth()*fieldSize,editor.getHeight()*fieldSize);
        animationCanvas = new Canvas();
        selectionCanvas = new Canvas();
        animationCanvas.widthProperty().bind(staticCanvas.widthProperty());
        animationCanvas.heightProperty().bind(staticCanvas.heightProperty());
        selectionCanvas.widthProperty().bind(staticCanvas.widthProperty());
        selectionCanvas.heightProperty().bind(staticCanvas.heightProperty());
        this.board = new Board(staticCanvas,animationCanvas,editor.getMap(),editor.getTheme(),fieldSize);
        this.canvasGroup = new Group(animationCanvas,staticCanvas,selectionCanvas);
    }

    public void update(){
        reloadMap();
        View.drawBoard(board,editor.getMap(),board.getTheme(),false);
    }

    public Scene getScene() {
        return scene;
    }

    public Canvas getStaticCanvas() {
        return staticCanvas;
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
        HBox cropInputs = createAndGetCropInputs();
        Label difficultLabel = new Label("Schwierigkeit");
        Label goalLabel = new Label("Ziele");
        Label nameLabel = new Label("Levelname");
        Label sizeLabel = new Label("Levelgröße");
        Group labels = new Group(difficultLabel,goalLabel,nameLabel,sizeLabel);
        for(Node node : labels.getChildren()) node.setStyle("-fx-font-weight:bold");
        VBox difficultBox = new VBox();
        difficultBox.setSpacing(5);
        HBox gemBox = new HBox();
        gemBox.setSpacing(5);
        HBox timeBox = new HBox();
        timeBox.setSpacing(5);
        initGoalsInput();
        difficultInput = new TextField();
        difficultInput.setMaxWidth(40);
        difficultInput.setPromptText(">=0");
        difficultBox.getChildren().add(difficultInput);
        gemBox.getChildren().add( new Label("Gems"));
        timeBox.getChildren().add(new Label("Zeit"));

        for(TextField entry: gemInputs){
            gemBox.getChildren().add(entry);
        }

        for(TextField entry: timeInputs){
            timeBox.getChildren().add(entry);
        }

        loadBox = new ComboBox<String>();
        loadBox.promptTextProperty().setValue("laden...");
        saveButton = new Button("Speichern");
        exitButton = new Button("Zurück zum Menü");
        informationRoot.getChildren().addAll(nameLabel,nameInput, sizeLabel,cropInputs,
                difficultLabel, difficultBox, goalLabel, gemBox, timeBox,loadBox, saveButton,exitButton);
        rootPane.setLeft(informationRoot);

    }

    public void selectFeld(int column, int row){

        Map<Property,Integer> map;
        try {
            map = editor.getLevel().getFeld(column,row).getProperties();
            ObservableList<Map.Entry<Property,Integer>> list = FXCollections.observableArrayList();
            selectFeldOnSelectionCanvas(row,column);
            for(Map.Entry<Property,Integer> entry : map.entrySet()){
                list.add(entry);
            }
            tableView.setItems(list);
        }
        catch(ClassCastException e){
            e.printStackTrace();
            tableView.setItems(null);
        }

    }

    private void selectFeldOnSelectionCanvas(int column, int row) {
        GraphicsContext gc = selectionCanvas.getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.clearRect(0,0,selectionCanvas.getWidth(),selectionCanvas.getHeight());
        gc.setTransform(staticCanvas.getGraphicsContext2D().getTransform());

        double startX = column * fieldSize;
        double startY = row * fieldSize;
        gc.setFill(new Color(0.43, 0.43,0.36,0.6));
        gc.setStroke(Color.web("ffab32"));
        gc.setLineWidth(2);
        gc.fillRect(startX,startY,fieldSize,fieldSize);
        gc.strokeRect(startX,startY,fieldSize,fieldSize);
    }

    private void initPropertyBox(){
        VBox propertyBoxRoot = new VBox();
        ObservableList<Map.Entry<Property,Integer>> list = FXCollections.observableArrayList();
        tableView = new TableView();
        tableView.setEditable(true);
        tableView.setItems(list);

        TableColumn<Map.Entry<Property,Integer>,String> propertyName =  new TableColumn<>("Property");
        propertyName.setCellValueFactory(getCellFactoryProps());

        TableColumn<Map.Entry<Property,Integer>,String> propertyValue = new TableColumn<>("Wert");
        propertyValue.setCellValueFactory(getCellFactoryValue());
        propertyValue.setEditable(true);

        propertyValue.setCellFactory(TextFieldTableCell.forTableColumn());
        propertyValue.setOnEditCommit(e -> {
            Map.Entry<Property,Integer> entry = e.getTableView().getItems().get(e.getTablePosition().getRow());
           try {
               Integer newVal = Integer.parseInt(e.getNewValue());
               entry.setValue(newVal);
           }
           catch(NumberFormatException exception){

           }
        });

        tableView.getColumns().setAll(propertyName,propertyValue);
        propertyBoxRoot.getChildren().add(tableView);

        HBox addButtons = new HBox();
        selectPropertyBox = new ComboBox<Property>();
        ObservableList<Property> propertyList = FXCollections.observableArrayList();
        for(Property prop: Property.values()) if(!prop.isGlobal()) propertyList.add(prop);
        selectPropertyBox.setItems(propertyList);
        selectPropertyBox.setPromptText("Property");
        propertyValueInput = new TextField();
        propertyValueInput.setPrefColumnCount(3);
        addPropertyButton = new Button("Hinzufügen");
        addButtons.getChildren().addAll(selectPropertyBox,propertyValueInput,addPropertyButton);
        propertyBoxRoot.getChildren().add(addButtons);
        rootPane.setRight(propertyBoxRoot);

    }


    private void initGoalsInput(){
        difficultInput = new TextField();
        gemInputs = new TextField[3];
        timeInputs = new TextField[3];
        for (int i = 0; i<gemInputs.length;i++){
            gemInputs[i] = getGemTimeInput(30);
            timeInputs[i] = getGemTimeInput(40);
        }
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

    public Button getExitButton(){
        return this.exitButton;
    }


    public TextField getDifficultInputField() {
        return difficultInput;
    }

    private TextField getGemTimeInput(double size){
        TextField inputField = new TextField();
        inputField.setPrefWidth(size);
        return inputField;
    }

    public double getFieldSize(){
        return  this.fieldSize;
    }

    public ToggleGroup getModeButtons() {
        return modeButtons;
    }

    public Button getAddPropertyButton() {
        return addPropertyButton;
    }

    public TextField getPropertyValueInput() {
        return propertyValueInput;
    }

    public ComboBox getSelectPropertyBox() {
        return selectPropertyBox;
    }

    public TableView getTableView(){
        return  this.tableView;
    }

    public ComboBox<String> getLoadBox() {
        return loadBox;
    }

    public TextField getColInput() {
        return colInput;
    }

    public TextField getRowInput() {
        return rowInput;
    }

    public Button getCropButton() {
        return cropButton;
    }

    private Callback<TableColumn.CellDataFeatures<Map.Entry<Property,Integer>,String>,ObservableValue<String>> getCellFactoryProps(){
       return param-> new SimpleStringProperty(param.getValue().getKey().name());
    }

    private Callback<TableColumn.CellDataFeatures<Map.Entry<Property,Integer>,String>,ObservableValue<String>> getCellFactoryValue(){
        return param-> new SimpleStringProperty(param.getValue().getValue().toString());
    }

    public void reloadMap() {
        this.board = new Board(staticCanvas,animationCanvas,editor.getMap(),editor.getTheme(),fieldSize);
        staticCanvas.setWidth(editor.getWidth()*this.fieldSize);
        staticCanvas.setHeight(editor.getHeight()*this.fieldSize);

    }

    public Canvas getSelectionCanvas() {
        return selectionCanvas;
    }
}
