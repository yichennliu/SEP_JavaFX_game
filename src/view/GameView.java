package view;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import model.enums.Medal;
import model.enums.Property;
import model.enums.SandUhr;
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
    private ImageView timerIcons;
    private Label timer;
    private HBox timeRewardInfo;
    private Label restGem;
    private Label currentGems;
    private ImageView gemIcon;
    private ImageView currentMedal;

    public GameView(Stage stage, Level level) {
        root = new Group();

        this.sceneGame = new Scene(this.root);
        this.stage = stage;
        this.width = stage.getWidth();
        this.height = stage.getHeight();
        this.level = level;
        stylesheet = MenuView.fileToStylesheetString(new File("src/view/style.css"));
        sceneGame.getStylesheets().add(stylesheet);
        staticCanvas = new Canvas(width, height - 40);
        Canvas animatedCanvas = new Canvas(staticCanvas.getWidth(), staticCanvas.getHeight());

        Group canvasGroup = new Group(staticCanvas, animatedCanvas);

        root.getChildren().addAll(canvasGroup);
        stage.setTitle("BoulderDash - " + this.level.getName());
        try {
            this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        } catch (Exception e) {
            System.out.println("Theme-Import-Fail: " + e.getMessage());
        }

        this.timeRewardInfo = new HBox(10);
        this.currentGems = new Label();
        this.gemIcon = new ImageView();
        this.timer = new Label();
        this.restGem = new Label();
        this.currentMedal = new ImageView();
        this.currentMedal.setFitHeight(30);
        this.currentMedal.setFitWidth(30);
        this.timerIcons = new ImageView();
        this.timerIcons.setFitHeight(30);
        this.timerIcons.setFitWidth(30);
        this.createHboxStyle();
        this.showMedalInfo();
        this.showCurrentSandUhr();
        this.showCollectedGems();
        this.createDiamondIcons();
        timeRewardInfo.getChildren().addAll(timerIcons, timer, gemIcon, currentGems, currentMedal, restGem);
        root.getChildren().addAll(timeRewardInfo);
        this.board = new Board(staticCanvas, animatedCanvas, level.getMap(), theme, fieldSize);
        this.update();

        if (!stage.isShowing()) stage.show();
    }

    public Canvas getCanvas() {
        return this.staticCanvas;
    }

    public Scene getScene() {
        return this.sceneGame;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void update() {

        this.scrollToMe();
        board.stopAnimation();
        View.drawBoard(this.board, level.getMap(), this.theme, true);
        this.showCollectedGems();
        this.showMedalInfo();
        this.showCurrentSandUhr();

    }

    /*if ME is at the edge of the viewport, a new translation will be set (starts indirectly tranlsation transition)*/
    private void scrollToMe() {
        Feld meFeld = level.whereAmI();
        if (meFeld == null) return;

        double canvasHeight = board.getHeight();
        double canvasWidth = board.getWidth();
        double newTranslateX = 0;
        double newTranslateY = 0;
        double relativeMeX = meFeld.getColumn() * fieldSize;
        double relativeMeY = meFeld.getRow() * fieldSize;
        Affine transformation = this.board.getTransformation();
        Point2D meOnCanvas = transformation.transform(relativeMeX, relativeMeY);

        double meOnCanvasX = meOnCanvas.getX();
        double meOnCanvasY = meOnCanvas.getY();

        if (meOnCanvasX < canvasWidth * 0.2 && reverseTransform(0, 0).getX() > 0) {
            newTranslateX = canvasWidth * 0.2 - meOnCanvasX;
        }
        if (meOnCanvasX > canvasWidth * 0.8 && reverseTransform(canvasWidth, 0).getX() < this.level.getWidth() * fieldSize) {
            newTranslateX = canvasWidth * 0.8 - meOnCanvasX;
        }
        if (meOnCanvasY < canvasHeight * 0.2) {
            newTranslateY = canvasHeight * 0.2 - meOnCanvasY;
        }
        if (meOnCanvasY > canvasHeight * 0.8) {
            newTranslateY = canvasHeight * 0.8 - meOnCanvasY;
        }

        if (newTranslateX != 0.0 || newTranslateY != 0.0) {
            this.board.translate(new Translate(newTranslateX, newTranslateY));
        }
    }

    /*gets a relative coordinate based on given input and affine transformation parameters*/
    private Point2D reverseTransform(double j, double k) {
        Affine transformation = this.board.getTransformation();
        double a = transformation.getMxx();
        double b = transformation.getMxy();
        double c = transformation.getTx();
        double d = transformation.getMyx();
        double e = transformation.getMyy();
        double f = transformation.getTy();
        double x = (b * f - b * k - c * e + e * j) / (a * e - b * d);
        double y = (a * k + c * d - d * j - a * f) / (a * e - b * d);
        return new Point2D(x, y);
    }

    public void zoom(double delta, double factor) {
        Affine transformation = this.board.getTransformation();

        if (delta < 0) {
            transformation.append(new Scale(1 / factor, 1 / factor));
        } else transformation.append(new Scale(factor, factor));
        this.board.applyTransformation(transformation);
    }

    private void createHboxStyle() {
        this.timeRewardInfo.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
                + "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color: black;"
                + "-fx-background-color: black;");
        this.timeRewardInfo.setSpacing(20);
        this.timeRewardInfo.setAlignment(Pos.CENTER);
        this.timeRewardInfo.toFront();
        this.timeRewardInfo.setPrefHeight(50);
        this.timeRewardInfo.setPrefWidth(width);
    }

    private void createDiamondIcons() {
        final Image gem = new Image(GameView.class.getResourceAsStream("images/Diamand.png"));
        this.gemIcon.setImage(gem);
        this.gemIcon.setFitHeight(30);
        this.gemIcon.setFitWidth(30);
    }

    public Label getTimerLabel() {
        return this.timer;
    }

    private void showCollectedGems() {
        Integer result = this.level.getPropertyValue(Property.GEMS);
        currentGems.setText(result.toString());
        currentGems.setTextFill(Color.WHITE);
    }

    private void setCountToGoldInfo() {
        int showRemainingGemsGoldInfo = this.level.getRemainingGemsToGold();
        restGem.setText("Needed Gems to Gold Medal: " + showRemainingGemsGoldInfo);
        restGem.setTextFill(Color.WHITE);

    }

    private void setCountToSilverInfo() {
        int showRemainingGemsSilverInfo = this.level.getRemainingGemsToSilver();
        restGem.setText("Needed Gems to Silver Medal: " + showRemainingGemsSilverInfo);
        restGem.setTextFill(Color.WHITE);

    }


    private void setCurrentMedal(Medal medalType) {

        switch (medalType) {
            case GOLD:
                currentMedal.setImage(Medal.GOLD.getMedalImage());
                break;
            case BRONZE:
                currentMedal.setImage(Medal.BRONZE.getMedalImage());
                break;
            case SILVER:
                currentMedal.setImage(Medal.SILVER.getMedalImage());
                break;
        }

    }

    private void showMedalInfo() {

        if (level.getPropertyValue(Property.GEMS) < level.getGemGoals()[0] && level.getPropertyValue(Property.TICKS) >= level.getTickGoals()[2] ||
                level.getPropertyValue(Property.GEMS) < level.getGemGoals()[1] && level.getPropertyValue(Property.TICKS) >= level.getTickGoals()[1] ||
                level.getPropertyValue(Property.GEMS) < level.getGemGoals()[2] && level.getPropertyValue(Property.TICKS) >= level.getTickGoals()[0]) {

            restGem.setTextFill(Color.WHITE);
            restGem.setText("No chance to get Medals!");
        }

        if (level.getPropertyValue(Property.GEMS) >= level.getGemGoals()[0] && level.getPropertyValue(Property.TICKS) <= level.getTickGoals()[2]) {
            this.setCountToSilverInfo();
            this.setCurrentMedal(Medal.BRONZE);

        }

        if (level.getPropertyValue(Property.GEMS) >= level.getGemGoals()[1] && level.getPropertyValue(Property.TICKS) <= level.getTickGoals()[1]) {
            this.setCountToGoldInfo();
            this.setCurrentMedal(Medal.SILVER);

        }

        if (level.getPropertyValue(Property.GEMS) >= level.getGemGoals()[2] && level.getPropertyValue(Property.TICKS) <= level.getTickGoals()[0]) {
            restGem.setText("You've got gold!");
            this.setCurrentMedal(Medal.GOLD);
        }


    }


    private void setCurrentSandUhr(SandUhr sandUhrType) {

        switch (sandUhrType) {
            case RED:
                this.timerIcons.setImage(SandUhr.RED.getSandUhrImage());
                break;
            case GREEN:
                this.timerIcons.setImage(SandUhr.GREEN.getSandUhrImage());
                break;
            case YELLOW:
                this.timerIcons.setImage(SandUhr.YELLOW.getSandUhrImage());
                break;
        }

    }

    private void showCurrentSandUhr() {

        double currentsecond = this.level.getPropertyValue(Property.TICKS) / 5;
        double totalSecond = this.level.getTickGoals()[0] / 5;

        if (currentsecond / totalSecond >= 0.3) {
            this.setCurrentSandUhr(SandUhr.YELLOW);

        } else {
            this.setCurrentSandUhr(SandUhr.GREEN);

        }
        if (currentsecond / totalSecond >= 0.6) {
            this.setCurrentSandUhr(SandUhr.RED);
        }

    }

}











