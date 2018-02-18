package controller;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import model.game.MedalStatus;
import view.MenuView;
import java.util.ArrayList;
import java.util.Map;

public class MenuController {

    private MenuView menuView;
    private Map<String, MedalStatus> medalStatuses;
    private Controller controller;
    private ToggleGroup group;

    public MenuController(MenuView menuView, Map<String, MedalStatus> medalStatuses, Controller controller){
        this.menuView = menuView;
        this.medalStatuses = medalStatuses;
        this.controller = controller;
        addMenuViewComponents();
        chooseLevel();

    }

    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
        addMenuViewComponents();

    }

    private void addMenuViewComponents() {
      menuView.getContentFrame().getGameButton().setOnAction(e -> {
            this.controller.startGame();
        });

     menuView.getContentFrame().getThemeEditorButton().setOnAction(e -> {
            this.controller.startThemeEditor();
        });

        }

     private void chooseLevel(){

         ArrayList <Button> levelButtons = menuView.getContentFrame().getListlevelButtons();
            Button level ;


         for(int i=0; i<levelButtons.size();i++){
                level= levelButtons.get(i);

             final String path= (String) level.getUserData();
                 level.setOnAction(e -> {
                 this.controller.startLevel("src/json/level/"+path);
             });

         }






    }
}
