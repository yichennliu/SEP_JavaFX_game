package view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Game {

    private GraphicsContext gameGC;
    private Canvas gameCanvas;
    private Scene scene;
    private Stage stage;
    private double width, height;
    private Group root;
    private Affine transformation = new Affine();


    public Game(Stage stage){
        root = new Group();

        this.scene = new Scene(this.root);
        this.stage = stage;
        this.width = stage.getWidth();
        this.height = stage.getHeight();


        gameCanvas = new Canvas(width,height);
        gameGC = gameCanvas.getGraphicsContext2D();

        root.getChildren().addAll(gameCanvas);
    }

    public Canvas getCanvas(){
        return this.gameCanvas;
    }

    public Scene getScene(){
        return this.scene;
    }

    public void translate(double x, double y){
        this.transformation.prepend(new Translate(x,y));
        this.gameGC.setTransform(this.transformation);
    }

    public void zoom(double delta, double factor){

        if(delta < 0){
            this.transformation.append(new Scale(1/factor, 1/factor));
        }
        else this.transformation.append(new Scale(factor,factor));
        this.gameGC.setTransform(this.transformation);
    }

}

