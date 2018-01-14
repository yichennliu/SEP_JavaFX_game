package main;

import view.View;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Feld;


public class Controller {
    private View view;
    private Feld feld;

    public Controller(View view, Feld feld) {
        this.view = view;
        this.feld = feld;

        Stage gamestage= this.view.getStage();

        //???System.out.println does not work:
        //System.out.println("ABCD")

        gamestage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode().equals(KeyCode.UP)) {

                System.out.println("Direction NORTH");

                if(event.isShiftDown()){
                    System.out.println("Feld graben");
                }
            }

            if (event.getCode().equals(KeyCode.DOWN)) {

                System.out.println("Direction SOUTH");

                if (event.isShiftDown()) {
                    System.out.println("Feld graben");
                }
            }

            if (event.getCode().equals(KeyCode.LEFT)) {
                if (event.isShiftDown()) {
                    System.out.println("Feld graben");
                }

                System.out.println("Direction WEST");
            }

            if (event.getCode().equals(KeyCode.RIGHT)) {
                if (event.isShiftDown()) {
                    System.out.println("Feld graben");
                }

                System.out.println("Direction EAST");
            }

            this.view.update(View.Mode.GAME);
        });


        gamestage.addEventHandler(KeyEvent.KEY_RELEASED, event->{
            if(event.getCode().equals(KeyCode.UP)) {
               System.out.println("Released:"+InputDirection.GOUP.toString());

            }

            if(event.getCode().equals(KeyCode.DOWN)){

                System.out.println("Released:"+InputDirection.GODOWN.toString());
            }
            if(event.getCode().equals(KeyCode.LEFT)){

                System.out.println("Released:"+InputDirection.GODOWN.toString());
            }
            if(event.getCode().equals(KeyCode.RIGHT)){

                System.out.println("Released:"+InputDirection.GODOWN.toString());
            }

        });

    }

}
