package controller;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import view.MenuView;

import java.util.List;


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
        chooseLevel();
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
        });}

     private void chooseLevel(){


         List <ToggleButton> levelButtons = menuView.getLevelButtons();
            ToggleButton level ;

         for(int i=0; i<levelButtons.size();i++){
                level= levelButtons.get(i);

             final String path= (String) level.getUserData();

                 level.setOnAction(e -> {
                 this.menuController.startLevel(path);
             });

         }






    }
}
