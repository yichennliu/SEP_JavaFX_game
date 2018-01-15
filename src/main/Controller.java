package main;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Feld;
import model.Level;
import model.enums.InputDirection;
import view.View;

public class Controller {
    private View view;
    private Level level;
    private View.Mode currentMode;

    public Controller(View view, Level level){
        this.view=view;
        this.level=level;
        this.currentMode = View.Mode.GAME;

        Stage gamestage= this.view.getStage();
        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if(event.getCode().equals(KeyCode.UP)&& event.isShiftDown()|event.getCode().equals(KeyCode.DOWN)&&event.isShiftDown()
                    |event.getCode().equals(KeyCode.LEFT)&&event.isShiftDown()|
                    event.getCode().equals(KeyCode.RIGHT)&&event.isShiftDown()){

                System.out.println("Feld graben");
            }

            if (event.getCode().equals(KeyCode.UP)) { System.out.println("Direction NORTH"); }

            if (event.getCode().equals(KeyCode.DOWN)) { System.out.println("Direction SOUTH"); }

            if (event.getCode().equals(KeyCode.LEFT)) { System.out.println("Direction WEST"); }

            if (event.getCode().equals(KeyCode.RIGHT)) { System.out.println("Direction EAST"); }

            this.view.update(View.Mode.GAME);
        });


        gamestage.addEventHandler(KeyEvent.KEY_RELEASED, event->{

            if(event.getCode().equals(KeyCode.UP)) {
                System.out.println("Released:"+ InputDirection.GOUP.toString());

            }

            if(event.getCode().equals(KeyCode.DOWN)){

                System.out.println("Released:"+InputDirection.GODOWN.toString());
            }
            if(event.getCode().equals(KeyCode.LEFT)){

                System.out.println("Released:"+InputDirection.GOLEFT.toString());
            }
            if(event.getCode().equals(KeyCode.RIGHT)){

                System.out.println("Released:"+InputDirection.GORIGHT.toString());
            }

        });

        EventHandler<ActionEvent> loop = e -> {
            this.level.tick();
            this.view.update(currentMode);
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(1/5),loop);
    }


}
