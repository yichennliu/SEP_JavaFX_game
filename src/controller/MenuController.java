package controller;

import javafx.scene.control.Button;
import model.game.MedalStatus;
import view.MenuView;
import java.util.ArrayList;
import java.util.Map;

public class MenuController {

    private MenuView menuView;
    private Map<String, MedalStatus> medalStatuses;
    private Controller controller;

    public MenuController(MenuView menuView, Map<String, MedalStatus> medalStatuses, Controller controller){
        this.menuView = menuView;
        this.medalStatuses = medalStatuses;
        this.controller = controller;
        this.addMenuViewComponents();
        this.chooseLevel();

    }

    /**
     * @return Status der Medaillen. Key ist der Pfad des Levels (level.getJsonPath()),
     *         Value is ein Objekt in dem steht welche Medaillen für dieses Level erreicht wurden
     */
    public Map<String, MedalStatus> getMedalStatuses() {
        return this.medalStatuses;
    }


    public void setMenuView(MenuView menuView) {
        this.menuView = menuView;
        this.addMenuViewComponents();

    }

    private void addMenuViewComponents() {
        menuView.getContentFrame().getGameButton().setOnAction(e -> {
            this.controller.startGame();
        });

        menuView.getContentFrame().getLevelEditorButton().setOnAction(e -> {
            this.controller.startLevelEditor();

        });

        menuView.getContentFrame().getThemeEditorButton().setOnAction(e -> {
            this.controller.startThemeEditor();
        });

    }

     private void chooseLevel() {
        ArrayList <Button> levelButtons = menuView.getContentFrame().getListlevelButtons();
        Button level ;

        for (int i=0; i<levelButtons.size();i++){
            level = levelButtons.get(i);

            final String path= (String) level.getUserData();
                level.setOnAction(e -> {
                    this.controller.startLevel("src/json/level/"+path);

            });
        }
    }
}
