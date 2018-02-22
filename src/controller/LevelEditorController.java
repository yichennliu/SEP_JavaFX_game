package controller;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import main.LevelFactory;
import model.enums.Property;
import model.enums.Token;
import model.game.Feld;
import model.game.Level;
import model.levelEditor.LevelEditor;
import view.levelEditor.LevelEditorView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LevelEditorController {

    private Controller controller;
    private LevelEditor editor;
    private LevelEditorView levelEditorView;
    private boolean mouseDown = false;
    private Feld selectedFeld;
    private Point2D lastCoordinate;
    private boolean meSet;

    public LevelEditorController(Controller controller, LevelEditor editor, LevelEditorView levelEditorView){

        this.controller = controller;
        this.editor = editor;
        this.levelEditorView = levelEditorView;

        initInputEvents();
        loadLevel("src/json/level/bewegung.json");

    }



    private void exit(){
        this.controller.startMenu();
    }

    private void loadLevel(String path){
        try{
            Level level  = LevelFactory.importLevel(path);
            if(level!=null){
                this.editor.resetLevel(level);
                this.levelEditorView.reloadMap();
                this.levelEditorView.getNameInput().textProperty().setValue(level.getName());
                if(level.whereAmI()!=null) this.meSet = true;
                TextField[] gemInputs = this.levelEditorView.getGemInputs();
                TextField[] timeInputs = this.levelEditorView.getTimeInputs();
                for(int i = 0; i<gemInputs.length; i++){
                    gemInputs[i].setText(level.getGemGoals()[i]+"");
                    timeInputs[i].setText(level.getTickGoals()[i]+"");
                }
                this.levelEditorView.selectFeld(0,0);
                this.levelEditorView.update();
            }
            else System.out.println("Importfehler");
            /*TODO: import von REgeln etc..*/
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private void initInputEvents() {
        levelEditorView.getModeButtons().selectedToggleProperty().addListener((a,b,c) -> {
            if(c!=null) editor.setMode((LevelEditor.Mode) c.getUserData());
        });

       Canvas staticCanvas = this.levelEditorView.getStaticCanvas();

        staticCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            mouseDown = true;
            int col = (int) (e.getY()/levelEditorView.getFieldSize());
            int row = (int) (e.getX()/levelEditorView.getFieldSize());
            if(editor.getMode()==LevelEditor.Mode.BRUSH){
                lastCoordinate = new Point2D(col,row) ;
                brushFeld(col,row);
                this.levelEditorView.update();
            }
            else {
                this.levelEditorView.selectFeld(col,row);
                this.selectedFeld = editor.getLevel().getFeld(col,row);
            }
        });
        staticCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if(mouseDown){
                int col = (int) (e.getY() / levelEditorView.getFieldSize());
                int row = (int) (e.getX() / levelEditorView.getFieldSize());
                if(editor.getMode()==LevelEditor.Mode.BRUSH) {
                    Point2D newCoord = new Point2D(col, row);
                    if (!lastCoordinate.equals(newCoord)) {
                        lastCoordinate = newCoord;
                        brushFeld(col, row);
                        this.levelEditorView.update();
                    }
                }
                else {
                    Point2D newCoord = new Point2D(col, row);
                    if (lastCoordinate!=null && !lastCoordinate.equals(newCoord)){
                        this.levelEditorView.selectFeld(col,row);
                        this.selectedFeld = editor.getLevel().getFeld(col,row);
                        lastCoordinate = newCoord;
                    }

                }
            }
        });
        staticCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            mouseDown = false;
        });
        staticCanvas.setCursor(Cursor.HAND);

        levelEditorView.getExitButton().setOnAction(e -> {
            this.exit();
        });

        ToggleGroup buttons = this.levelEditorView.getHeaderButtons();
        buttons.selectedToggleProperty().addListener((a,b,c) -> {
            this.editor.setCurrentToken((Token) c.getUserData());
        });
        buttons.selectToggle(buttons.getToggles().get(0));

        TextField propertyValueInput = levelEditorView.getPropertyValueInput();
        ComboBox propertySelectionBox = levelEditorView.getSelectPropertyBox();
        this.levelEditorView.getAddPropertyButton().setOnAction(e -> {
            Property prop = (Property) propertySelectionBox.getSelectionModel().getSelectedItem();
            Integer value = stringToInteger(propertyValueInput.getText());
            if(prop!=null && value!= null){
                if (value<0) value= 0;
                selectedFeld.setPropertyValue(prop,value);
                levelEditorView.selectFeld(selectedFeld.getRow(),selectedFeld.getColumn());
                propertyValueInput.setStyle("-fx-text-fill:black");
            }
            else if(value==null) propertyValueInput.setStyle("-fx-text-fill:red");
        });
        this.levelEditorView.getSaveButton().setOnAction(e-> {
            Level level = this.editor.getLevel();
            try {
                LevelFactory.exportLevel(level,"src/json/level/testExport.json");
            }
            catch(IOException ioException){
                System.out.println("Fehler beim Export " + ioException.getMessage());
            }
        });

        initLevelSettingsInput();
    }

    private void cropMap(int rows, int cols) {
        Feld[][] newMap = new Feld[rows][cols];
        int oldLength = editor.getMap()[0].length;
        int oldHeight = editor.getMap().length;

        for(int row = 0; row<rows; row++){
            for(int col = 0; col<cols; col++){
                boolean isInOldRange = (col<oldLength && row < oldHeight);
                Feld oldFeld = isInOldRange ? editor.getMap()[row][col] : null;
                Token token  = isInOldRange ? oldFeld.getToken() : Token.MUD;
                Map<Property,Integer> map = isInOldRange ? oldFeld.getProperties() : new HashMap<>();
                Feld newFeld = new Feld(token,map,col,row);
                newFeld.setLevel(editor.getLevel());
                newMap[row][col] = newFeld;
            }
        }
    }

    private void brushFeld(int column, int row){
        Token token = editor.getCurrentToken();
        Token oldToken = this.editor.getMap()[column][row].getToken();
        if(token == Token.ME ){
            if (meSet) return;
            else meSet = true;
        }
        else if(oldToken==Token.ME) meSet = false;
        this.editor.getMap()[column][row].setToken(editor.getCurrentToken());
        this.levelEditorView.update();
    }

    private void initLevelSettingsInput(){
        levelEditorView.getNameInput().textProperty().addListener((a,b,c) -> {
            if(c!=null) this.editor.setName(c);
        });

        TextField[] gemGoals = this.levelEditorView.getGemInputs();
        TextField[] timeGoals = this.levelEditorView.getTimeInputs();

        for(int i = 0; i< gemGoals.length; i++){
            int x = i;
            TextField gemInput = gemGoals[i];
            TextField timeInput = timeGoals[i];
            gemInput.textProperty().addListener( (a,b,c) -> {
                Integer value = stringToInteger(c);
                if(value!=null) {
                    System.out.println(value + " " + x);
                    this.editor.setGemGoal(x,value);
                    gemInput.setStyle("-fx-text-fill:black");
                }
                else gemInput.setStyle("-fx-text-fill:red");
            });
            timeGoals[i].textProperty().addListener( (a,b,c) -> {
                Integer value = stringToInteger(c);
                if(value!=null) {
                    this.editor.setTimeGoal(x,value);
                    timeInput.setStyle("-fx-color:black");
                }
                else timeInput.setStyle("-fx-color:red");
            });
        }

    }


    private Integer stringToInteger(String string){
        try {
            return Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            return null;
        }
    }

}
