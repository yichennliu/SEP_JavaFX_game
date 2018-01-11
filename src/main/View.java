package main;

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
    private GraphicsContext  editorGC;

    private Stage stage;

    private Scene menu;
    private Scene levelEditor;
    private Scene game;

    private Level level;
    private int maxSize = 1000;

    private double windowWidth = 800;
    private double windowHeight = 600;
    private double fieldSize = 20;

    private double translateX;
    private double translateY;
    private double scale =1;

    private Affine transformation;

    public enum Mode {EDITOR, GAME, MENU};

    public View(Level level, Stage stage){
        this.level = level;
        this.stage = stage;

        /*initialize root-Groups for different scenes */
        Group menuRoot = new Group();
        Group editorRoot = new Group();
        Group gameRoot = new Group();

        /* initialize Scenes (menu, game, leveleditor */
        menu = new Scene(menuRoot);
        levelEditor = new Scene(editorRoot);
        game = new Scene(gameRoot);

        gameCanvas = new Canvas(windowWidth,windowHeight);
        gameGC = gameCanvas.getGraphicsContext2D();

        transformation = new Affine();

        gameCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {

            if(e.getButton()== MouseButton.PRIMARY){
                transformation.prepend(new Translate(-10,0));
                System.out.println("PRIMARY!!!");
            }
            else {
                transformation.prepend(new Translate(10,0));
                System.out.println("Secondary!!!");
            }
            this.drawMap(this.gameCanvas,this.gameGC);
        });

        gameCanvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            double scale = 1.5;
            if(e.getDeltaY()<0){
             scale = 1/scale;
            }

            transformation.append(new Scale(scale,scale));
            this.drawMap(this.gameCanvas,this.gameGC);
        });

       /* add Scene-Content to scene-Roots */
       gameRoot.getChildren().addAll(gameCanvas);
       menuRoot.getChildren().addAll();
       editorRoot.getChildren().addAll();

       stage.setScene(this.game);
       stage.setTitle("BoulderDash - "+this.level.getName());
       stage.show();
    }

    private void showMenu(){
        stage.setScene(this.menu);
    }

    private void showEditor(){
        stage.setScene(this.levelEditor);
    }

    private void showGame(){
        stage.setScene(this.game);
    }

    public void update(Mode mode) {
        switch (mode) {
            case GAME:
                    showGame();
                    drawMap(gameCanvas,gameGC);
                    break;
            case EDITOR:
                    showEditor();
                    break;
            case MENU:
                    showMenu();;
                    break;
        }
    }

    private void drawMap(Canvas canvas, GraphicsContext gc){
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setTransform(this.transformation);

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
                this.gameGC.fillText(text == "MUD" ? "" : text,xPos+fieldSize/2-15,yPos+fieldSize/2+5);
            }
        }
    }

}
