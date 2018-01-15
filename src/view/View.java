package view;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import model.Level;

public class View {

    private Canvas gameCanvas;
    private Canvas editorCanvas;

    private GraphicsContext gameGC;
    private GraphicsContext editorGC;

    private Stage stage;

    private Menu menuScene;
    private Scene levelEditor;
    private Game gameScene;

    private Level level;
    private int maxSize = 1000;

    private double windowWidth = 800;
    private double windowHeight = 600;
    private double fieldSize = 20;

    private double translateX;
    private double translateY;
    private double scale =1;

    private Affine transformation;

    public enum Mode {EDITOR, GAME, MENU}

    public View(Level level, Stage stage){
        this.level = level;
        this.stage = stage;
        this.stage.setWidth(windowWidth);
        this.stage.setHeight(windowHeight);
        this.stage.centerOnScreen();

        /* init Scene-Content */
       this.menuScene = new Menu(this.stage);
       this.gameScene = new Game(this.stage);

       stage.setScene(gameScene.getScene());
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
                    showMenu();
                    break;
        }
    }

    private void drawMap(Canvas canvas, GraphicsContext gc){
        Affine actualTransformation = gc.getTransform();
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setTransform(actualTransformation);

        int mapWidth = level.getWidth();
        int mapHeight = level.getHeight();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum* fieldSize;
                double yPos = rowNum*fieldSize;
                gc.strokeRect(xPos, yPos, fieldSize, fieldSize);
                String text = level.getFeld(rowNum, colNum).toString();
                // Erdreich verstecken, um Ausrichtungstest in text.json zu sehen
                gc.fillText(text.equals("MUD") ? "" : text,xPos+fieldSize/2-15,yPos+fieldSize/2+5);
            }
        }
    }

    public Stage getStage(){ return this.stage; }

}
