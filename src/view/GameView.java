package view;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.enums.Property;
import model.game.Feld;
import model.game.Level;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

import java.io.File;

public class GameView {

    private GraphicsContext gameGC;
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

    private HBox  timeRewardInfo;
    private Label timer;
    private Label restGem;
    private Label currentGems;
    private Label currentMedal;

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
        stylesheet= MenuView.fileToStylesheetString(new File("src/view/style.css"));
        sceneGame.getStylesheets().add(stylesheet);
        staticCanvas = new Canvas(width,height-40);
        Canvas animatedCanvas = new Canvas(staticCanvas.getWidth(),staticCanvas.getHeight());
        gameGC = staticCanvas.getGraphicsContext2D();
        //this.board = new Board(staticCanvas,animatedCanvas,fieldSize);

        Group canvasGroup = new Group(staticCanvas,animatedCanvas);

        root.getChildren().addAll(canvasGroup);
        stage.setTitle("BoulderDash - " + this.level.getName());
        this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");

        this.timeRewardInfo = new HBox(10);
        this.currentGems = new Label();
        this.timer = new Label();
        this.restGem = new Label();
        this.currentMedal = new Label();
        setHboxStyle();
        showMedalInfo();
        showCollectedGems();
        timeRewardInfo.getChildren().addAll(timer, currentGems, currentMedal, restGem);
        root.getChildren().addAll(timeRewardInfo);
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
        showCollectedGems();
        showMedalInfo();

    }

    private void scrollToMe(){
        Feld meFeld = level.whereAmI();
        if(meFeld==null) return;

        double canvasHeight = board.getHeight();
        double canvasWidth = board.getWidth();
        double newTranslateX= 0;
        double newTranslateY = 0;
        double relativeMeX = meFeld.getColumn()*fieldSize + 0.5 * fieldSize;
        double relativeMeY = meFeld.getRow()*fieldSize + 0.5 * fieldSize;
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
            System.out.println("TranslatioN!");
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

    public void rotate(double i) {
        Affine transformation = this.board.getTransformation();
        double fieldSize= 15.0;
        double levelWidth  = this.level.getWidth();
        double levelHeight = this.level.getHeight();
        transformation.append(new Rotate(i,levelWidth*fieldSize/2,levelHeight*fieldSize/2))  ;
        this.board.applyTransformation(transformation);
    }

    public void setHboxStyle(){
        this.timeRewardInfo.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
                + "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color: brown;"
                + "-fx-background-color: brown;");

        this.timeRewardInfo.setSpacing(20);
        this.timeRewardInfo.setAlignment(Pos.CENTER);
        this.timeRewardInfo.toFront();
        this.timeRewardInfo.setPrefHeight(50);
        this.timeRewardInfo.setPrefWidth(width);
    }

    public Label updateTimerLabel(){
        return this.timer;
    }

    public void showCollectedGems(){
        int result= this.level.getPropertyValue(Property.GEMS);
        currentGems.setText("Gems got: "+ result);
    }

    public void setCountToGoldInfo() {
        Pair<Integer, Integer> showInfo = level.getRemainingGoldTicksGems();
        restGem.setText("Needed Gems to Gold: "+showInfo.getValue());

    }

    public void setCountToSilverInfo(){
        Pair<Integer, Integer> showInfo= level.getRemainingSilverTickGems();
        restGem.setText("Needed Gems to Silver :"+showInfo.getValue());

    }

    public void setCountToBronzeInfo(){
        Pair<Integer, Integer> showInfo= level.getRemainingBronzeTickGems();
        restGem.setText("Needed Gems to Bronze: "+showInfo.getValue());

    }

    public void showMedalInfo(){
        if(level.getPropertyValue(Property.GEMS)>=level.getGemGoals()[0]){
            setCountToSilverInfo();
            currentMedal.setText("Current Medal: Bronze");

        }
        else if (level.getPropertyValue(Property.GEMS)>=level.getGemGoals()[1]){
            setCountToGoldInfo();
            currentMedal.setText("Current Medal: Silver");
        }

        else if(level.getPropertyValue(Property.GEMS)>=level.getGemGoals()[2]){
            currentMedal.setText("You've got Gold!");
        }

        else{
            setCountToBronzeInfo();
            currentMedal.setText("No medal :(");
        }
    }

}

