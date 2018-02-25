package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.LevelFactory;
import model.ai.AI;
import model.ai.Robot;
import model.enums.*;
import model.game.Level;
import model.game.MedalStatus;
import model.themeEditor.Theme;
import org.json.JSONObject;
import view.EndGameAlert;
import view.GamePausedAlert;
import view.GameView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameController {

    private Controller controller;
    private GameView gameView;
    private Level level;
    private Timeline timeline;
    private EscapeButtonHandler handler;
    private AI robot;
    private boolean robotActive;
    private List<Media> audios;
    private List<Theme> themes;
    private int themeIndex = 0;
    private int audioIndex =-1;
    private MediaPlayer player;

    public GameController(Level level, GameView gameView, Controller controller) {
        this.controller = controller;
        this.gameView = gameView;
        this.level = level;
        this.addDirectionEvents();
        this.addEscapeGameMenu();
        this.robot = new Robot(level, 5);
        this.convertGameModus();
        this.addDragEvent();
        this.addPauseResumeGameEvents();
        this.addStopAudioEvent();
        this.addThemeChangeEvent();
        this.initAudios();
        this.startAudio();
        themes = this.controller.getThemes();
        setNextTheme();
    }

    private void setNextTheme() {
        Theme theme = getNextTheme();
        this.gameView.setNewTheme(theme);
    }

    private void robotize(boolean activate) {
        this.robotActive = activate;
    }

    private void initAudios(){
        audios = new ArrayList<>();
        for(String path : new File("src/audio").list()){
            try {
                audios.add(new Media(new File("src/audio/"+path).toURI().toString()));
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    private Theme getNextTheme(){
        if(themes!=null){
            Theme returnTheme = themes.get(themeIndex);
            themeIndex = (themeIndex==themes.size()-1) ? 0 : themeIndex+1;
            return returnTheme;
        }
        return null;

    }

    private Media getNextClip(){
        if(audios==null||audios.size()==0) return null;
        audioIndex = (audios.size()-1==audioIndex) ? 0 : audioIndex+1;
        return audios.get(audioIndex);
    }

    private void startAudio(){
        Media audio = getNextClip();
        if(audio!=null){
            stopAudio();
            player = new MediaPlayer(audio);
            player.onEndOfMediaProperty().setValue(() -> {
                this.startAudio();
            });
            player.play();
        }
    }

    private void stopAudio(){
        if(player!=null) player.stop();
    }

    private void pauseAudio(){
        if(player!=null) player.pause();
    }

    private void resumeAudio(){
        if(player!=null) player.play();
    }


    public void update() {
        this.startAudio();
        this.addDirectionEvents();
    }

    public void tick() {
        EventHandler<ActionEvent> loop = e -> {
            System.out.println("tick " + this.level.getPropertyValue(Property.TICKS));
            this.updateTimerLabel(this.level.getPropertyValue(Property.TICKS));
            this.updateSandUhr(this.level.getPropertyValue(Property.TICKS));
            this.updateMedalInfo();
            this.updateCollectedGems();
            if (robotActive) this.level.setInputDirection(robot.getNextMove());
            boolean killedPre;
            boolean killedMain;
            boolean killedPost;

            /* Compute a tick */
            this.level.resetProperties();
            this.level.execPreRules();
            this.level.executeMainRules();
            this.level.execPostRules();
            this.level.checkLosing();

            this.level.setInputDirection(null);
            this.gameView.update();
            this.level.tick();

            if (this.level.getWinningStatus() != WinningStatus.PLAYING) {
                // save medal
                if (this.level.getWinningStatus() == WinningStatus.WON) {
                    this.saveMedal();
                    this.endOfGameDialog();
                } else if(this.level.getWinningStatus() == WinningStatus.LOST){
                    this.endOfGameDialog();
                }

            }
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1.0 / 5.0), loop);
        this.timeline = new Timeline(frame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();

    }

    private void updateTimerLabel(Integer currentTick){
        Label timer = this.gameView.getTimerLabel();
        Integer maxSecs = this.level.getTickGoals()[0]/5;
        int currentSec = (currentTick/5);
        int timeLeft = maxSecs - currentSec;
        timer.setText("Time Left: "+timeLeft);

        if(timeLeft == 0 && this.level.getWinningStatus() == WinningStatus.LOST){
            this.endOfGameDialog();
        }
        if(timeLeft<=10){
            timer.setTextFill(Color.RED);
        } else{
            timer.setTextFill(Color.WHITE);
        }

    }


    private void updateCollectedGems() {
        Integer result = this.level.getPropertyValue(Property.GEMS);
        this.gameView.getCurrentGems().setText(result.toString());
        this.gameView.getCurrentGems().setTextFill(Color.WHITE);

    }


    private void updateMedalInfo() {

        if (this.level.getCurrentMedal() == null) {
            this.gameView.setCountToBronzeInfo();

        } else if (this.level.getCurrentMedal() == Medal.BRONZE) {
            this.gameView.setCountToSilverInfo();
            this.gameView.setCurrentMedal(Medal.BRONZE);

        } else if (this.level.getCurrentMedal() == Medal.SILVER) {
            this.gameView.setCountToGoldInfo();
            this.gameView.setCurrentMedal(Medal.SILVER);

        } else if (this.level.getCurrentMedal() == Medal.GOLD) {
            this.gameView.getRestGem().setText("You've got gold! Now exit to win!");
            this.gameView.getRestTicks().setTextFill(Color.BLACK);
            this.gameView.setCurrentMedal(Medal.GOLD);
        }

    }

    private void updateSandUhr(Integer currentTick){
        double maxSec = (double) this.level.getTickGoals()[0]/5;
        double currentSec = (double) currentTick/5;
        double timePast = currentSec/maxSec;

        if(timePast >=0.3){
            this.gameView.setCurrentSandUhr(SandUhr.YELLOW);
        } else {
            this.gameView.setCurrentSandUhr(SandUhr.GREEN);
        }

        if (timePast >=0.6) {
            this.gameView.setCurrentSandUhr(SandUhr.RED);
        }

    }

    public GameView getGameView() {
        return gameView;
    }

    private void addAlertKeyEvent(Alert alert) {

        EventHandler<KeyEvent> pressEnter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
                    ((Button) event.getTarget()).fire();
                }
            }
        };

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(button
                -> button.addEventHandler(KeyEvent.KEY_PRESSED, pressEnter));

    }

    private void convertGameModus() {
        Stage gameStage = this.gameView.getStage();
        gameStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.K)) {
                this.robot = new Robot(level, 5);
                this.robotize(true);

            } else {
                this.robotize(false);
            }
        });

    }

    private void addStopAudioEvent(){
        Stage gameStage = this.gameView.getStage();
        gameStage.addEventHandler(KeyEvent.KEY_PRESSED,event ->{
            if(event.getCode().equals(KeyCode.M)) {
                stopAudio();
            } else if(player==null && event.getCode().equals(KeyCode.M)) {
                System.out.println("Audio an!");
                startAudio();
            }
        });
    }

    private void addThemeChangeEvent(){
        Stage gameStage = this.gameView.getStage();
        gameStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.T)){
                setNextTheme();
            }
        });
    }


    private void addPauseResumeGameEvents() {
        Stage gameStage = this.gameView.getStage();
        gameStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                if (timeline != null && timeline.getStatus().equals(Animation.Status.RUNNING)) {
                    this.gameView.createPauseGameIcon();
                    this.pauseAudio();
                    timeline.stop();

                } else if (timeline != null && timeline.getStatus() == Animation.Status.STOPPED) {
                    this.gameView.removePauseGameIcon();
                    this.resumeAudio();
                    timeline.play();
                }
            }
        });
    }


    public void addEscapeGameMenu() {
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
        this.pauseAudio();

        EndGameAlert endGameAlert = new EndGameAlert();

        if (this.level.getWinningStatus() == WinningStatus.WON) {
            endGameAlert.setHeaderText("You successfully completed the level \"" + this.level.getName() + "\". Hooray!");
            endGameAlert.getButtonTypes().setAll(endGameAlert.getNextLevelButton());

        } else if(this.level.getWinningStatus() == WinningStatus.LOST) {
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
                this.stopAudio();
                this.controller.startMenu();

            }

            if( result.get() == endGameAlert.getNextLevelButton()) {
                gameView.getStage().removeEventHandler(KeyEvent.KEY_PRESSED, handler);
                this.startAudio();
                this.controller.startNextLevel();
            }

        });
    }

    private void addDirectionEvents() {
        Stage gamestage = this.gameView.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (robotActive) return;
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

    private void addDragEvent() {
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
    private void saveGame() {
        try {
            String[] originalPath = this.level.getJsonPath().split("/");
            String originalFileName = originalPath[originalPath.length - 1];
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

    /**
     * Save current medal
     */
    private void saveMedal() {
        Map<String, MedalStatus> medalStatuses = this.controller.getMenuController().getMedalStatuses();
        MedalStatus medalStatus = medalStatuses.get(this.level.getJsonPath());
        if (medalStatus == null) {
            medalStatus = new MedalStatus();
        }
        medalStatus.set(this.level.getCurrentMedal());
        medalStatuses.put(this.level.getJsonPath(), medalStatus);

        // save as json
        JSONObject jsonMedalStatuses = new JSONObject();
        for (Map.Entry<String, MedalStatus> entry : medalStatuses.entrySet()) {
            MedalStatus status = entry.getValue();
            JSONObject jsonStatus = new JSONObject();
            jsonStatus.put("bronze", status.has(Medal.BRONZE));
            jsonStatus.put("silver", status.has(Medal.SILVER));
            jsonStatus.put("gold", status.has(Medal.GOLD));
            jsonMedalStatuses.put(entry.getKey(), jsonStatus);
        }

        // write
        try {
            PrintWriter out = new PrintWriter("src/json/medals/medalstatuses.json");
            out.print(jsonMedalStatuses.toString(4));
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Medal couldn't be saved: " + e.getMessage());
        }
    }


    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setLevel(Level level) {
        this.level = level;
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
                }

                pauseAudio();

                GameController.this.addAlertKeyEvent(alert);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == alert.getSaveButton()) {
                    GameController.this.saveGame();
                    resumeAudio();
                    alert.close();
                    if (timeline != null) {
                        timeline.play();
                    }
                } else if (result.get() == alert.getExitButton()) {
                    gamestage.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    stopAudio();
                    GameController.this.controller.startMenu();


                } else if (result.get() == alert.getRetryButton()) {
                    GameController.this.controller.startLevel(level.getJsonPath());
                    startAudio();
                    alert.close();
                    timeline.playFromStart();


                } else if (result.get() == alert.getCancelButton()) {
                    alert.close();
                    resumeAudio();
                    if (timeline != null) {
                        timeline.play();
                    }
                }
            }
        }
    }


}

