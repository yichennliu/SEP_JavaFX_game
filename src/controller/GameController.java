package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.enums.InputDirection;
import model.game.Level;
import view.GameView;

public class GameController {

    private Controller menuController;
    private GameView gameView;
    private Level level;

    public GameController(Level level, GameView gameView, Controller menuController){
        this.menuController = menuController;
        this.gameView = gameView;
        this.level = level;

        Stage gamestage= this.gameView.getStage();

        gamestage.addEventHandler(ScrollEvent.SCROLL, e -> {
            this.gameView.zoom(e.getDeltaY(), 1.5);
            this.gameView.update();
        });

        gamestage.heightProperty().addListener((a,b,c) -> {
            this.gameView.getCanvas().setHeight(c.doubleValue());
            this.gameView.getCanvas().setWidth(gamestage.getWidth());
        });

        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode().equals(KeyCode.UP)) {
                if(event.isShiftDown()){
                    this.level.setInputDirection(InputDirection.DIGUP);
                }

                this.level.setInputDirection(InputDirection.GOUP);
            }

            if (event.getCode().equals(KeyCode.DOWN)){
                if(event.isShiftDown()){
                    this.level.setInputDirection(InputDirection.DIGDOWN);
                }
                this.level.setInputDirection(InputDirection.GODOWN);
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                if(event.isShiftDown()){
                    this.level.setInputDirection(InputDirection.DIGLEFT);
                }
                this.level.setInputDirection(InputDirection.GOLEFT);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                if(event.isShiftDown()){
                    this.level.setInputDirection(InputDirection.DIGRIGHT);
                }
                this.level.setInputDirection(InputDirection.GORIGHT);
            }

            this.gameView.update();
        });


        EventHandler<ActionEvent> loop = e -> {
            /* Compute a tick */
            //this.level.executePre();
            this.level.executeMainRules();
            //this.level.executePost();
            this.gameView.update();
            //this.level.tick();
            this.level.setInputDirection(null);
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1.0/5.0),loop);
        Timeline tl = new Timeline(frame);
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }
}

