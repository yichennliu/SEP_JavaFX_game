package controller;

import view.PrimaryPage;

/**
 * Created by aidabakhtiari on 16.01.18.
 */
public class PrimaryController {

    private PrimaryPage primaryPage;
    private Object model;
    private Controller primaryController;

    public PrimaryController(PrimaryPage primaryPage, Controller menuController) {
        this.primaryPage = primaryPage;
        //this.model = model;
        this.primaryController = menuController;
        addPrimaryPageComponents();
    }


    public void setPrimaryPage(PrimaryPage primaryPage) {
        this.primaryPage = primaryPage;
        addPrimaryPageComponents();
    }

    private void addPrimaryPageComponents() {
        primaryPage.getName().setOnAction(e -> {
            this.primaryController.startMenu();
        });

        primaryPage.getNoName().setOnAction(e -> {
            this.primaryController.startMenu();
        });
    }

}
