package view;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import model.Level;

public class View {

    private Canvas gameCanvas;
    private Canvas editorCanvas;

    private GraphicsContext gameGC;
    private GraphicsContext  editorGC;

    private Stage stage;

    private Menu menuScene;
    private Scene levelEditor;
    private Game gameScene;
    private ThemeEditorView themeEditorScene;

    private Level level;
    private int maxSize = 1000;

    private double windowWidth = 800;
    private double windowHeight = 600;
    private double fieldSize = 20;

    private double translateX;
    private double translateY;
    private double scale =1;

    private Affine transformation;

    public enum Mode {EDITOR, GAME, MENU, THEME};

    public View(Level level, Stage stage){
        this.level = level;
        this.stage = stage;
        this.stage.setWidth(windowWidth);
        this.stage.setHeight(windowHeight);

        this.stage.centerOnScreen();

        /*Test für ThemeEditor*/
        ThemeEditor themeEditor = new ThemeEditor();
        this.themeEditorScene = new ThemeEditorView(this.stage,themeEditor);
        ThemeEditorController themeEditorController = new ThemeEditorController(this.themeEditorScene, themeEditor);

        /* init Scene-Content */
       this.menuScene = new Menu(this.stage);
       this.gameScene = new Game(this.stage);

       stage.setScene(themeEditorScene.getScene());
       stage.setTitle("BoulderDash - "+this.level.getName());

       Canvas canvas = gameScene.getCanvas();
       canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
           gameScene.translate(10,0);
           update(Mode.GAME);
       });
        canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            gameScene.zoom(e.getDeltaY(),1.5);
            update(Mode.GAME);
        });
        stage.show();
    }

    private void showMenu(){
        stage.setScene(this.menuScene.getScene());
    }

    private void showEditor(){
        stage.setScene(this.levelEditor);
    }

    private void showGame(){
        stage.setScene(this.gameScene.getScene());
    }

    private void showTheme() {
        stage.setScene(this.themeEditorScene.getScene());
    }


    public void update(Mode mode) {
        switch (mode) {
            case GAME:
                    showGame();
                    Canvas gameCanvas = gameScene.getCanvas();
                    drawMap(gameCanvas,gameCanvas.getGraphicsContext2D());
                    break;
            case EDITOR:
                    showEditor();
                    break;
            case MENU:
                    showMenu();;
                    break;
            case THEME:
                    showTheme();
                    break;
        }
    }


    private void drawMap(Canvas canvas, GraphicsContext gc){
        Affine actualTransformation = gc.getTransform();
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setTransform(actualTransformation);

        int mapWidth = level.getMap()[0].length;
        int mapHeight = level.getMap().length;
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum* fieldSize;
                double yPos = rowNum*fieldSize;
                gc.strokeRect(xPos, yPos, fieldSize, fieldSize);
                String text = level.getMap()[rowNum][colNum].toString();
                // Erdreich verstecken, um Ausrichtungstest in text.json zu sehen
                gc.fillText(text == "MUD" ? "" : text,xPos+fieldSize/2-15,yPos+fieldSize/2+5);
            }
        }
    }

    public Stage getStage(){
        return this.stage;
    }

}
