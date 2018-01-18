package controller;

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


        PrimaryPage.getName().setOnAction(e -> {
            this.primaryController.startMenu();
        });

       PrimaryPage.getNoName().setOnAction(e -> {
            this.primaryController.startMenu();
        });
    }


}
