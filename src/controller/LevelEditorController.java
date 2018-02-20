package controller;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import main.LevelFactory;
import model.enums.Token;
import model.game.Level;
import model.levelEditor.LevelEditor;
import view.levelEditor.LevelEditorView;
import java.io.IOException;

public class LevelEditorController {

    private Controller controller;
    private LevelEditor editor;
    private LevelEditorView levelEditorView;

    public LevelEditorController(Controller controller, LevelEditor editor, LevelEditorView levelEditorView){

        this.controller = controller;
        this.editor = editor;
        this.levelEditorView = levelEditorView;

        initInputEvents();
//        TODO: loadLevel("src/json/level/bewegung.json");
    }

    private void loadLevel(String path){
        try{
            Level level  = LevelFactory.importLevel(path);
            if(level!=null){
                this.editor.setMap(level.getMap());
                this.levelEditorView.getNameInput().textProperty().setValue(level.getName());
                this.levelEditorView.update();
                /*TODO: Level komplett importieren -> GemGoals und so*/
            }
            else System.out.println("Importfehler");
            /*TODO: import von REgeln etc..*/
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private void initInputEvents() {
        this.levelEditorView.getStaticCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            Point2D point = this.levelEditorView.getBoard().reverseTransform(e.getX(),e.getY());
           this.editor.getMap()[(int) (point.getY()/20.0) ][(int) (point.getX()/20.0)].setToken(editor.getCurrentToken());
           this.levelEditorView.update();
        });
        ToggleGroup buttons = this.levelEditorView.getHeaderButtons();
        buttons.selectedToggleProperty().addListener((a,b,c) -> {
            this.editor.setCurrentToken((Token) c.getUserData());
        });
        buttons.selectToggle(buttons.getToggles().get(0));

        this.levelEditorView.getStaticCanvas().setCursor(Cursor.HAND);

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
                Integer value = StringToInteger(c);
                if(value!=null) {
                    System.out.println(value + " " + x);
                    this.editor.setGemGoal(x,value);
                    gemInput.setStyle("-fx-text-fill:black");
                }
                else gemInput.setStyle("-fx-text-fill:red");
            });
            timeGoals[i].textProperty().addListener( (a,b,c) -> {
                Integer value = StringToInteger(c);
                if(value!=null) {
                    this.editor.setTimeGoal(x,value);
                    timeInput.setStyle("-fx-color:black");
                }
                else timeInput.setStyle("-fx-color:red");
            });
        }

    }


    private Integer StringToInteger(String string){
        try {
            return Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            return null;
        }
    }

}
