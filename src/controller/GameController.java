package controller;

import javafx.animation.KeyFrame;
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
//            this.level.tick();
            this.executePre();
            this.executeMain();
            this.executePost();
            this.gameView.update();
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1/5),loop);
    }

    public void executePre(){}

    public void executeMain(){}

    public void executePost(){}
}

