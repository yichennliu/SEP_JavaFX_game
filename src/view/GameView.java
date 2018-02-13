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

    private Canvas staticCanvas;
    private Scene sceneGame;
    private Stage stage;
    private double width, height;
    private Group root;
    private Theme theme;
    private Level level;
    private String stylesheet;
    private double fieldSize = 60.0;
    private Board board;

    public GameView(Stage stage, Level level){
        root = new Group();

        this.sceneGame = new Scene(this.root);
        this.stage = stage;
        this.width = stage.getWidth();
        this.height = stage.getHeight();
        this.level = level;
        stylesheet= PrimaryPage.fileToStylesheetString(new File("src/view/style.css"));
        sceneGame.getStylesheets().add(stylesheet);
        staticCanvas = new Canvas(width,height-40);
        Canvas animatedCanvas = new Canvas(staticCanvas.getWidth(),staticCanvas.getHeight());

        Group canvasGroup = new Group(staticCanvas,animatedCanvas);

        root.getChildren().addAll(canvasGroup);
        stage.setTitle("BoulderDash - " + this.level.getName());
        try {
            this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        }
        catch(Exception e){
            System.out.println("Theme-Import-Fail: " + e.getMessage());
        }

        this.board = new Board(staticCanvas,animatedCanvas, level.getMap(),theme,fieldSize);

        this.update();

        if(!stage.isShowing()) stage.show();
    }


    public Canvas getCanvas(){
        return this.staticCanvas;
    }

    public Scene getScene(){
        return this.sceneGame;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void update(){
        scrollToMe();
        board.stopAnimation();
        View.drawBoard(this.board,level.getMap(),this.theme,true);

    }

    private void scrollToMe(){
        Feld meFeld = level.whereAmI();
        if(meFeld==null) return;

        double canvasHeight = board.getHeight();
        double canvasWidth = board.getWidth();
        double newTranslateX= 0;
        double newTranslateY = 0;
        double relativeMeX = meFeld.getColumn()*fieldSize;
        double relativeMeY = meFeld.getRow()*fieldSize;
        Affine transformation = this.board.getTransformation();
        Point2D meOnCanvas =  transformation.transform(relativeMeX,relativeMeY);

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

        if(newTranslateX!=0.0 || newTranslateY!=0.0){
            this.board.translate(new Translate(newTranslateX,newTranslateY));
        }


    }

    /*gets a relative coordinate based on given input and affine transformation parameters*/
    private Point2D reverseTransform(double j, double k){
        Affine transformation = this.board.getTransformation();
        double a = transformation.getMxx(); double b = transformation.getMxy();
        double c = transformation.getTx(); double d = transformation.getMyx();
        double e = transformation.getMyy(); double f = transformation.getTy();
        double x = (b*f - b*k - c*e +e*j) / (a*e - b*d);
        double y = ( a*k + c*d - d*j -a*f) / (a*e -b*d);
        return new Point2D(x,y);
    }

    public void zoom(double delta, double factor){
        Affine transformation = this.board.getTransformation();

        if(delta < 0){
           transformation.append(new Scale(1/factor, 1/factor));
        }
        else transformation.append(new Scale(factor,factor));
        this.board.applyTransformation(transformation);
    }

}

