package main;


import model.Level;
import view.View;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class Controller {
    private View view;
    private Level level;

    public Controller(Level level, View view) {
        this.view = view;
        this.level= level;
        Stage gamestage= this.view.getStage();

        //System.out.println(gamestage.getX());
        //???System.out.println does not work???(Works only in view class)

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
               System.out.println("Released:"+InputDirection.GOUP.toString());

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


    }

}
