package controller;

import view.MenuView;

public class MenuController {

    private MenuView menuView;
    private Object model; // Todo: Menu-Model erstellen
    private Controller menuController;

    public MenuController(MenuView menuView, Object model, Controller menuController){
        this.menuView = menuView;
        this.model = model;
        this.menuController = menuController;

        menuView.getGameButton().setOnAction(e -> {
            this.menuController.startGame();
        });

        menuView.getThemeEditorButton().setOnAction(e -> {
            this.menuController.startThemeEditor();
        });
    }
}
