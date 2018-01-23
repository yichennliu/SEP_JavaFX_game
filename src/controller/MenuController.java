package controller;

import javafx.scene.control.ToggleGroup;
import view.MenuView;


public class MenuController {

    private MenuView menuView;
    private Object model; // Todo: Menu-Model erstellen
    private Controller menuController;
    private ToggleGroup group;


    public MenuController(MenuView menuView, Object model, Controller menuController){
        this.menuView = menuView;
        this.model = model;
        this.menuController = menuController;
        addMenuViewComponents();
    }

    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
        addMenuViewComponents();
    }

    private void addMenuViewComponents() {
        menuView.getGameButton().setOnAction(e -> {
            this.menuController.startGame();
        });

        menuView.getThemeEditorButton().setOnAction(e -> {
            this.menuController.startThemeEditor();
        });



    }
}
