package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.LevelFactory;
import model.enums.InputDirection;
import model.enums.Property;
import model.game.Level;
import view.GameView;

import java.io.IOException;
import java.util.Optional;

public class GameController {

    private Controller menuController;
    private GameView gameView;
    private Level level;
    private Timeline timeline;


    public GameController(Level level, GameView gameView, Controller menuController){
        this.menuController = menuController;
        this.gameView = gameView;
        this.level = level;
        addAlertEvent();
        addDirectionEvents();
        addGameViewComponents();
    }

    public void tick() {
        EventHandler<ActionEvent> loop = e -> {
            System.out.println("tick " + this.level.getPropertyValue(Property.TICKS));
            boolean killedPre;
            boolean killedMain;
            boolean killedPost;

            /* Compute a tick */
            this.level.resetProperties();
            //killedPre = this.level.executePre();
            killedMain = this.level.executeMainRules();
            //killedPost = this.level.executePost();

            this.level.setInputDirection(null);
            this.gameView.update();
            this.level.tick();
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1.0/5.0),loop);
        this.timeline = new Timeline(frame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }


    public GameView getGameView() {
        return gameView;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
        addGameViewComponents();

    }

    private void addAlertEvent(){
        Stage gamestage = this.gameView.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.ESCAPE)) {
                if (timeline != null) {
                    timeline.stop();
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit or Save");
                alert.setHeaderText("Do you want to save or exit the game?");

                ButtonType save_button = new ButtonType("Save", ButtonBar.ButtonData.OTHER);
                ButtonType save_exit_button = new ButtonType("Save & Exit", ButtonBar.ButtonData.OTHER);
                ButtonType exit_button = new ButtonType("Exit", ButtonBar.ButtonData.OTHER);
                ButtonType cancel_button = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(save_button, save_exit_button,exit_button,cancel_button);

                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == save_button){
                    this.saveGame();
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                    }
                }

                if (result.get() == save_exit_button){
                    this.saveGame();
                    this.menuController.startPrimaryPage();
                }

                if(result.get() == exit_button) {
                    this.menuController.startPrimaryPage();
                }

                if(result.get() == cancel_button){
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                    }
                }

            }

        });

    }

    private void addDirectionEvents() {
        Stage gamestage = this.gameView.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode().equals(KeyCode.UP)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGUP);
                }

                this.level.setInputDirection(InputDirection.GOUP);
            }

            if (event.getCode().equals(KeyCode.DOWN)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGDOWN);
                }
                this.level.setInputDirection(InputDirection.GODOWN);
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGLEFT);
                }
                this.level.setInputDirection(InputDirection.GOLEFT);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGRIGHT);
                }
                this.level.setInputDirection(InputDirection.GORIGHT);
            }

            this.gameView.update();

        });
    }

    private void addGameViewComponents() {
        Stage gamestage = this.gameView.getStage();

        gamestage.addEventHandler(ScrollEvent.SCROLL, e -> {
            this.gameView.zoom(e.getDeltaY(), 1.5);
            this.gameView.update();
        });

        gamestage.heightProperty().addListener((a,b,c) -> {
            this.gameView.getCanvas().setHeight(c.doubleValue());
            this.gameView.getCanvas().setWidth(gamestage.getWidth());
        });

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

}

