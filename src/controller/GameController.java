package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.LevelFactory;
import model.enums.InputDirection;
import model.enums.Property;
import model.enums.WinningStatus;
import model.game.Level;
import view.GameView;


import java.io.IOException;
import java.util.Optional;

public class GameController {

    private Controller menuController;
    private GameView gameView;
    private Level level;
    private Timeline timeline;
    private Timeline timer;
    private final Integer startSecond;
    private Integer second;

    public GameController(Level level, GameView gameView, Controller menuController){
        this.menuController = menuController;
        this.gameView = gameView;
        this.level = level;
        startSecond= this.level.getTickGoals()[0]/5;
        this.second = startSecond;
        this.addIngameMenu();
        this.addDirectionEvents();
        this.addGameViewComponents();
        this.countDown();
    }

    public void update(){
        countDown();
        addGameViewComponents();
        addDirectionEvents();

    }

    public void tick() {
        EventHandler<ActionEvent> loop = e -> {
            System.out.println("tick " + this.level.getPropertyValue(Property.TICKS));
            boolean killedPre;
            boolean killedMain;
            boolean killedPost;

            /* Compute a tick */
            this.level.resetProperties();
            //this.level.executePre();
            this.level.executeMainRules();
            //this.level.executePost();

            this.level.setInputDirection(null);
            this.gameView.update();
            this.level.tick();

            if (this.level.getWinningStatus() == WinningStatus.WON) {
                this.endOfGameDialog(true);
            } else if (this.level.getWinningStatus() == WinningStatus.LOST) {
                this.endOfGameDialog(false);
            }

        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1.0/5.0),loop);
        this.timeline = new Timeline(frame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();

    }



    public void countDown() {
        Label countDownLabel = this.gameView.updateTimerLabel();
        this.timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        if (timer != null) {
            timer.stop();

        }

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                second--;
                countDownLabel.setText("Time Left: " + second.toString());
                countDownLabel.setTextFill(Color.WHITE);

                if (second <= 0) {
                    timer.stop();
                    }

                if (second <= 10) {
                    countDownLabel.setTextFill(Color.RED);
                    }

                }
            });

            timer.getKeyFrames().add(keyFrame);
            timer.playFromStart();
        }


    public GameView getGameView() {
        return gameView;
    }


    private void addIngameMenu(){
        Stage gamestage = this.gameView.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.ESCAPE)) {
                if (timeline != null) {
                    timeline.stop();
                    timer.stop();
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit or Save");
                alert.setHeaderText("Do you want to save or exit the game?");

                ButtonType save_button = new ButtonType("Save", ButtonBar.ButtonData.OTHER);
                ButtonType save_exit_button = new ButtonType("Save & Exit", ButtonBar.ButtonData.OTHER);
                ButtonType exit_button = new ButtonType("Exit", ButtonBar.ButtonData.OTHER);
                ButtonType retry_button = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
                ButtonType cancel_button = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(save_button, save_exit_button,exit_button,retry_button,cancel_button);

                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == save_button){
                    this.saveGame();
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                        timer.playFromStart();

                    }
                }

                if (result.get() == save_exit_button){
                    this.saveGame();
                    this.menuController.startMenu();
                }

                if(result.get() == exit_button) {
                    this.menuController.startMenu();
                }

                if (result.get() == retry_button) {
                    this.menuController.startLevel(level.getJsonPath());
                    timeline.playFromStart();
                }

                if(result.get() == cancel_button){
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                        timer.playFromStart();

                    }
                }

            }

        });

    }

    /**
     * Show end of game dialog
     */
    private void endOfGameDialog(boolean won) {
        this.timeline.stop();
        this.timer.stop();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(won ? "You won!" : "Game Over");
        if (won) {
            alert.setHeaderText("You successfully completed the level \""+this.level.getName()+"\". Hooray!");
        } else {
            alert.setHeaderText("You lost. Dont't worry, try again!");
        }

        ButtonType retry_button = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
        ButtonType cancel_exit_button = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(retry_button, cancel_exit_button);
        Controller menuControllerLocal = this.menuController;
        Level levelLOcal = this.level;

        // This "runlater" is necessary because alert.showAndWait is not allowed during animation
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == retry_button) {
                    menuControllerLocal.startLevel(levelLOcal.getJsonPath());
                }

                if (result.get() == cancel_exit_button) {
                    menuControllerLocal.startMenu();
                }
            }
        });
    }

    private void addDirectionEvents() {
        Stage gamestage = this.gameView.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            //KeyCombination combine = new KeyCombination() {}
            if (event.getCode().equals(KeyCode.UP)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGUP);
                }
                else {
                    this.level.setInputDirection(InputDirection.GOUP);

                }
            }

            if (event.getCode().equals(KeyCode.DOWN)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGDOWN);
                }

                else {
                    this.level.setInputDirection(InputDirection.GODOWN);
                }
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGLEFT);
                }
                else{

                    this.level.setInputDirection(InputDirection.GOLEFT);

                }
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGRIGHT);
                }
                else{

                    this.level.setInputDirection(InputDirection.GORIGHT);

                }
            }

        });
    }

    private void addGameViewComponents() {
        Stage gamestage = this.gameView.getStage();

        gamestage.addEventHandler(ScrollEvent.SCROLL, e -> {
            this.gameView.zoom(e.getDeltaY(), 1.5);
            this.gameView.update();
        });


        this.gameView.getCanvas().heightProperty().bind(gamestage.heightProperty());
        this.gameView.getCanvas().widthProperty().bind(gamestage.widthProperty());
    }

    /**
     * Spiel speichern
     */
    public void saveGame() {
        try {
            LevelFactory.exportLevel(this.level, "src/json/savegame/" + this.level.getName() + ".json");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Fehler beim Speichern");
            alert.setContentText(e.getStackTrace().toString());

            ButtonType buttonOK = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonOK);

            Optional<ButtonType> result = alert.showAndWait();
        }
    }


    public void setGameView(GameView gameView){
        this.gameView = gameView;

    }


    public void setLevel(Level level){
        this.level = level;
    }


}

