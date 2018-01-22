package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import model.game.Level;

public class GameView {

    private GraphicsContext gameGC;
    private Canvas gameCanvas;
    private Scene scene;
    private Stage stage;
    private double width, height;
    private Group root;
    private Affine transformation = new Affine();
    private Theme theme;
    private Level level;


    public GameView(Stage stage, Level level){
        root = new Group();

        this.scene = new Scene(this.root);
        this.stage = stage;
        this.width = stage.getWidth();
        this.height = stage.getHeight();
        this.level = level;

        gameCanvas = new Canvas(width,height);
        gameGC = gameCanvas.getGraphicsContext2D();

        root.getChildren().addAll(gameCanvas);
        stage.setTitle("BoulderDash - " + this.level.getName());
        this.update();
        if(!stage.isShowing()) stage.show();
    }

    public Canvas getCanvas(){
        return this.gameCanvas;
    }

    public Scene getScene(){
        return this.scene;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void update(){
        System.out.println("gameview update");
        View.drawMap(this.gameGC,level.getMap(),15.0, null);

    }

    public void translate(double x, double y){
        this.transformation.append(new Translate(x,y));
        this.gameGC.setTransform(this.transformation);
    }

    public void zoom(double delta, double factor){

        if(delta < 0){
            this.transformation.append(new Scale(1/factor, 1/factor));
        }
        else this.transformation.append(new Scale(factor,factor));
        this.gameGC.setTransform(this.transformation);
    }

    public void rotate(double i) {
        double fieldSize= 15.0;
        double levelWidth  = this.level.getWidth();
        double levelHeight = this.level.getHeight();
        this.transformation.append(new Rotate(i,levelWidth*fieldSize/2,levelHeight*fieldSize/2))  ;
        this.gameGC.setTransform(this.transformation);
    }
}

