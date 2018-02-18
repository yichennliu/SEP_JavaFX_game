package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
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
import view.EndGameAlert;
import view.GamePausedAlert;
import view.GameView;

import java.io.IOException;
import java.util.Optional;

public class GameController {

    private Controller controller;
    private GameView gameView;
    private Level level;
    private Timeline timeline;
    private Timeline timer;
    private EscapeButtonHandler handler;


    public GameController(Level level, GameView gameView, Controller menuController) {
        this.controller = menuController;
        this.gameView = gameView;
        this.level = level;
        this.addDirectionEvents();
        this.addInGameMenu();
        this.addGameViewComponents();
        this.countDown();
    }

    public void update() {
        countDown();
        addGameViewComponents();
        addDirectionEvents();

    }

    public void tick() {
        EventHandler<ActionEvent> loop = e -> {
//            System.out.println("tick " + this.level.getPropertyValue(Property.TICKS));
            boolean killedPre;
            boolean killedMain;
            boolean killedPost;

            /* Compute a tick */
            this.level.resetProperties();
            this.level.execPreRules();
            this.level.executeMainRules();
            this.level.execPostRules();

            this.level.setInputDirection(null);
            this.gameView.update();
            this.level.tick();

            if (this.level.getWinningStatus() != WinningStatus.PLAYING) {
                this.endOfGameDialog();
            }
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1.0 / 5.0), loop);
        this.timeline = new Timeline(frame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();

    }

    public void countDown() {
        Label countDownLabel = this.gameView.updateTimerLabel();
        final Integer startSecond = this.level.getTickGoals()[0] / 5;
        this.timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        if (timer != null) {
            timer.stop();

        }

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            int second = startSecond;

            @Override
            public void handle(ActionEvent event) {
                second--;
                countDownLabel.setText("Time Left: " + second);
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

    private void addAlertKeyEvent(Alert alert) {

        EventHandler<KeyEvent> fireOnEnter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
                    ((Button) event.getTarget()).fire();
                }
            }
        };

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, fireOnEnter));

    }

    private class EscapeButtonHandler implements EventHandler<KeyEvent> {

        private Stage gamestage;

        public EscapeButtonHandler(Stage gamestage) {
            this.gamestage = gamestage;
        }

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.ESCAPE)) {

                GamePausedAlert alert = new GamePausedAlert();

                if (timeline != null) {
                    timeline.stop();
                    timer.stop();
                }


                GameController.this.addAlertKeyEvent(alert);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == alert.getSaveButton()) {
                    GameController.this.saveGame();
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                        timer.playFromStart();
                    }
                } else if (result.get() == alert.getSaveExitButton()) {
                    gamestage.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    GameController.this.saveGame();
                    GameController.this.controller.startMenu();
                } else if (result.get() == alert.getExitButton()) {
                    gamestage.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    GameController.this.controller.startMenu();


                } else if (result.get() == alert.getRetryButton()) {
//                    gamestage.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
//                    gameView.getStage().removeEventHandler(KeyEvent.KEY_PRESSED, handler);

                    GameController.this.controller.startLevel(level.getJsonPath());
                    alert.close();
                    timeline.playFromStart();
                    timer.playFromStart();


                } else if (result.get() == alert.getCancelButton()) {
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                        timer.play();
                    }
                }
            }
        }
    }


    public void addInGameMenu() {
        Stage gamestage = this.gameView.getStage();
        if (handler == null) {
            handler = new EscapeButtonHandler(gamestage);
        }
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    /**
     * Show end of game dialog
     */
    private void endOfGameDialog() {
        this.timeline.stop();
        this.timer.stop();

        EndGameAlert endGameAlert = new EndGameAlert();

        if (this.level.getWinningStatus() == WinningStatus.WON) {
            endGameAlert.setHeaderText("You successfully completed the level \"" + this.level.getName() + "\". Hooray!");
        } else {
            endGameAlert.setHeaderText("You lost. Dont't worry, try again!");
        }

        GameController.this.addAlertKeyEvent(endGameAlert);

        // "runlater" is necessary because alert.showAndWait is not allowed during animation
        Platform.runLater(() -> {
            Optional<ButtonType> result = endGameAlert.showAndWait();

            if (result.get() == endGameAlert.getRetryButton()) {
                gameView.getStage().removeEventHandler(KeyEvent.KEY_PRESSED, handler);
                this.controller.startLevel(this.level.getJsonPath());
            }

            if (result.get() == endGameAlert.getCancelExitButton()) {
                gameView.getStage().removeEventHandler(KeyEvent.KEY_PRESSED, handler);
                this.controller.startMenu();
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
                } else {
                    this.level.setInputDirection(InputDirection.GOUP);

                }
            }

            if (event.getCode().equals(KeyCode.DOWN)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGDOWN);
                } else {
                    this.level.setInputDirection(InputDirection.GODOWN);
                }
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGLEFT);
                } else {

                    this.level.setInputDirection(InputDirection.GOLEFT);

                }
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                if (event.isShiftDown()) {
                    this.level.setInputDirection(InputDirection.DIGRIGHT);
                } else {

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
     * Levelfortschritt speichern
     */
    public void saveGame() {
        try {
            String[] originalPath = this.level.getJsonPath().split("/");
            String originalFileName = originalPath[originalPath.length-1];
            LevelFactory.exportLevel(this.level, "src/json/savegame/" + originalFileName);
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


    public void setGameView(GameView gameView) {
        this.gameView = gameView;

    }

    public void setLevel(Level level) {
        this.level = level;
    }


}

