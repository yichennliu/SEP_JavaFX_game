package view;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.*;
import javafx.scene.transform.Affine;
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
       stage.setScene(menuScene.getSceneMenu());
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

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if(event.getCode().equals(KeyCode.UP)&& event.isShiftDown()|event.getCode().equals(KeyCode.DOWN)&&event.isShiftDown()
                    |event.getCode().equals(KeyCode.LEFT)&&event.isShiftDown()|
                    event.getCode().equals(KeyCode.RIGHT)&&event.isShiftDown()){

                System.out.println("Feld graben");
            }

            if (event.getCode().equals(KeyCode.UP)) { System.out.println("Direction NORTH"); }

            if (event.getCode().equals(KeyCode.DOWN)) { System.out.println("Direction SOUTH"); }

            if (event.getCode().equals(KeyCode.LEFT)) { System.out.println("Direction WEST"); }

            if (event.getCode().equals(KeyCode.RIGHT)) { System.out.println("Direction EAST"); }


        });

        stage.show();
    }

    private void showMenu(){
        stage.setScene(this.menuScene.getSceneMenu());
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
