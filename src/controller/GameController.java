package controller;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Level;
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

            if(event.getCode().equals(KeyCode.UP)&& event.isShiftDown()|| event.getCode().equals(KeyCode.DOWN)&&event.isShiftDown()
                    || event.getCode().equals(KeyCode.LEFT)&&event.isShiftDown() ||
                    event.getCode().equals(KeyCode.RIGHT)&&event.isShiftDown()){

                System.out.println("Feld graben");
                return;
            }

            if (event.getCode().equals(KeyCode.UP)) gameView.translate(0,-10);
            if (event.getCode().equals(KeyCode.DOWN))gameView.translate(0,10);
            if (event.getCode().equals(KeyCode.LEFT)) gameView.translate(-10,0);
            if (event.getCode().equals(KeyCode.RIGHT)) gameView.translate(10,0);
            this.gameView.update();
        });


//        gamestage.addEventHandler(KeyEvent.KEY_RELEASED, event->{
//
//            if(event.getCode().equals(KeyCode.UP)) {
//                System.out.println("Released:"+ InputDirection.GOUP.toString());
//
//            }
//
//            if(event.getCode().equals(KeyCode.DOWN)){
//
//                System.out.println("Released:"+InputDirection.GODOWN.toString());
//            }
//            if(event.getCode().equals(KeyCode.LEFT)){
//
//                System.out.println("Released:"+InputDirection.GOLEFT.toString());
//            }
//            if(event.getCode().equals(KeyCode.RIGHT)){
//
//                System.out.println("Released:"+InputDirection.GORIGHT.toString());
//            }
//
//        });

        EventHandler<ActionEvent> loop = e -> {
//            this.level.tick();
            this.gameView.update();
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1/5),loop);
    }

}

