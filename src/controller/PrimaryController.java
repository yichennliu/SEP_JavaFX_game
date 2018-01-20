package controller;

import javafx.scene.control.TextField;
import view.PrimaryPage;

/**
 * Created by aidabakhtiari on 16.01.18.
 */
public class PrimaryController {

    private PrimaryPage primaryPage;
    private Object model;
    private Controller primaryController;



    public PrimaryController(PrimaryPage primaryPage, Controller menuController){
        this.primaryPage = primaryPage;
        this.model = model;
        this.primaryController = menuController;



        primaryPage.getName().setOnAction(e -> {
             String  playerNameInput=primaryPage.getPlayerName();
           this.primaryController.startMenu(playerNameInput);
        });

       primaryPage.getNoName().setOnAction(e -> {
            this.primaryController.startMenu("   ");
        });
    }


}
