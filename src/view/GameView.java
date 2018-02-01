package view;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import model.game.Feld;
import model.game.Level;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

import java.io.File;

public class GameView {

    private GraphicsContext gameGC;
    private Canvas gameCanvas;
    private Scene sceneGame;
    private Stage stage;
    private double width, height;
    private Group root;
    private Affine transformation = new Affine();
    private Theme theme;
    private Level level;
    private String stylesheet;
    private double fieldSize = 60.0;


    public GraphicsContext getGameGC() {
        return gameGC;
    }

    public GameView(Stage stage, Level level){
        root = new Group();

        this.sceneGame = new Scene(this.root);
        this.stage = stage;
        this.width = stage.getWidth();
        this.height = stage.getHeight();
        this.level = level;
        stylesheet= PrimaryPage.fileToStylesheetString(new File("src/view/style.css"));
        sceneGame.getStylesheets().add(stylesheet);
        gameCanvas = new Canvas(width,height-40);
        gameGC = gameCanvas.getGraphicsContext2D();

        root.getChildren().addAll(gameCanvas);
        stage.setTitle("BoulderDash - " + this.level.getName());
        this.update();
        this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
//        setInitialZoom();
        if(!stage.isShowing()) stage.show();
    }


    public Canvas getCanvas(){
        return this.gameCanvas;
    }

    public Scene getScene(){
        return this.sceneGame;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void update(){
        scrollToMe();
        View.drawMap(this.gameGC,level.getMap(),this.fieldSize, this.theme);

    }

    public void translate(double x, double y){
        this.transformation.append(new Translate(x,y));
        this.gameGC.setTransform(this.transformation);
    }

    private void scrollToMe(){
        Feld meFeld = level.whereAmI();
        if(meFeld==null) return;

        double canvasHeight = gameCanvas.getHeight();
        double canvasWidth = gameCanvas.getWidth();
        double newTranslateX= 0;
        double newTranslateY = 0;
        double relativeMeX = meFeld.getColumn()*fieldSize + 0.5 * fieldSize;
        double relativeMeY = meFeld.getRow()*fieldSize + 0.5 * fieldSize;
        Point2D meOnCanvas= this.transformation.transform(relativeMeX,relativeMeY);
        double meOnCanvasX = meOnCanvas.getX();
        double meOnCanvasY = meOnCanvas.getY();

        if(meOnCanvasX<canvasWidth*0.2 && reverseTransform(0,0).getX()>0) {
            newTranslateX = canvasWidth*0.2 - meOnCanvasX;
        }
        if(meOnCanvasX>canvasWidth*0.8 && reverseTransform(canvasWidth,0).getX()<this.level.getWidth()*fieldSize) {
            newTranslateX = canvasWidth*0.8 - meOnCanvasX;
        }
        if(meOnCanvasY<canvasHeight*0.2) {
            newTranslateY = canvasHeight*0.2 - meOnCanvasY;
        }
        if(meOnCanvasY>canvasHeight*0.8){
            newTranslateY = canvasHeight*0.8 - meOnCanvasY;
        }


           this.transformation.prepend(new Translate(newTranslateX,newTranslateY));
        this.gameGC.setTransform(this.transformation);
    }

    /*gets a relative coordinate based on given input and affine transformation parameters*/
    private Point2D reverseTransform(double j, double k){
        double a = transformation.getMxx(); double b = transformation.getMxy();
        double c = transformation.getTx(); double d = transformation.getMyx();
        double e = transformation.getMyy(); double f = transformation.getTy();
        double x = (b*f - b*k - c*e +e*j) / (a*e - b*d);
        double y = ( a*k + c*d - d*j -a*f) / (a*e -b*d);
        return new Point2D(x,y);
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

