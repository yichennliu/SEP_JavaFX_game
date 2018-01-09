package main;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Level;

public class View {

    private Canvas canvas;
    private GraphicsContext gc;
    private Group root;
    private Scene scene;
    private Level level;
    private int maxSize = 1000;
    private Stage stage;

    public View(Level level, Stage stage){
        this.level = level;
        this.stage = stage;
        root = new Group();
        scene = new Scene(this.root);
        double height = this.level.getMap().length;
        double width = this.level.getMap()[0].length;
        double ratio = height / width;
        canvas = ratio >= 1
                ? new Canvas(maxSize/ratio,maxSize)
                : new Canvas(maxSize,maxSize*ratio);
        gc = canvas.getGraphicsContext2D();

        root.getChildren().addAll(canvas);

        stage.setScene(this.scene);
        stage.setTitle("BoulderDash - "+this.level.getName());
        stage.show();
    }

    public void drawMap(){
        int mapWidth = level.getMap()[0].length;
        int mapHeight = level.getMap().length;
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double fieldLength = canvasWidth / mapWidth;
        double fieldHeight = canvasHeight / mapHeight;

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum*fieldLength;
                double yPos = rowNum*fieldHeight;
                this.gc.strokeRect(xPos, yPos, fieldLength, fieldHeight);
                String text = level.getMap()[rowNum][colNum].toString();
                // Erdreich verstecken um Ausrichtungstest in text.json zu sehen
                this.gc.fillText(text == "MUD" ? "" : text,xPos+fieldLength/2-15,yPos+fieldHeight/2+5);
            }
        }
    }

    public Stage getStage() {
        return this.stage;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    //Test1
    public void moveMeBy(int dx, int dy){

        if (dx == 0 && dy == 0) {
            return;
        }

        Circle me= new Circle();

        double cx = me.getBoundsInLocal().getWidth()/2;
        double cy = me.getBoundsInLocal().getHeight()/2;
        double x = cx + me.getLayoutX() + dx;
        double y = cy + me.getLayoutY() + dy;

        moveMeTo(x, y);
    }

    //Test2
    public void moveMeTo(double x, double y){

        int width= 600;
        int height= 400;

        Circle me = new Circle();

        double cx = me.getBoundsInLocal().getWidth()/2;
        double cy = me.getBoundsInLocal().getHeight()/2;

        if (x - cx >= 0 && x + cx <= width && y - cy >= 0 && y + cy <= height) {
            me.relocate(x - cx, y - cy);
        }

    }

}
