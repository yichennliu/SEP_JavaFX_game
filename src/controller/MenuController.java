package controller;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import view.MenuView;
import java.util.ArrayList;

public class MenuController {

    private MenuView menuView;
    private Object model; // Todo: Menu-Model erstellen
    private Controller menuController;
    private ToggleGroup group;

    public MenuController(MenuView menuView, Object model,Controller menuController){
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
      menuView.getContentFrame().getGameButton().setOnAction(e -> {
            this.menuController.startGame();
        });

     menuView.getContentFrame().getThemeEditorButton().setOnAction(e -> {
            this.menuController.startThemeEditor();
        });

        }

     private void chooseLevel(){

         ArrayList <Button> levelButtons = menuView.getContentFrame().getListlevelButtons();
            Button level ;


         for(int i=0; i<levelButtons.size();i++){
                level= levelButtons.get(i);

             final String path= (String) level.getUserData();
             System.out.println(path);
                 level.setOnAction(e -> {
                 this.menuController.startLevel("src/json/level/"+path);
             });

         }






    }
}
