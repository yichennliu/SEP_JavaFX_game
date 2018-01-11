package main;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Feld;
import view.View;

public class Controller {
    private View view;
    private Feld feld;

    public Controller(View view, Feld feld){
        this.view=view;
        this.feld=feld;
        Canvas canvas= this.view.getCanvas();
        Stage stage= this.view.getStage();

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {

                if(event.getCode().equals(KeyCode.UP)){
                    if (event.isShiftDown())
                    System.out.println("Direction NORTH");
                }

                if(event.getCode().equals(KeyCode.DOWN)){
                    System.out.println("Direction SOUTH");

                }

                if(event.getCode().equals(KeyCode.LEFT)) {
                    System.out.println("Direction WEST");
                }

                if(event.getCode().equals(KeyCode.RIGHT)){
                    System.out.println("Direction EAST");
                }
            }
        });


        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:

                }
            }
        });


    }


}
