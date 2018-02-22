package controller;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.Menu.SavedGameProgress;
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
        this.chooseSavedGame();

    }

    public void update(){
        this.chooseLevel();
        this.chooseSavedGame();
        this.setSavedGameButton();
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


    private void setSavedGameButton(){
        Button savedGameButton = this.menuView.getContentFrame().getSaveButton();
        VBox menuVbox = this.menuView.getContentFrame().getMenuVboxlinks();

        menuVbox.getChildren().add(savedGameButton);

    }

     private void chooseLevel() {
        ArrayList <Button> levelButtons = this.menuView.getContentFrame().getListlevelButtons();
        Button standardLevel ;

        for (int i=0; i<levelButtons.size();i++){
            standardLevel = levelButtons.get(i);

            final String path= (String) standardLevel.getUserData();
                standardLevel.setOnAction(e -> {
                    this.controller.startLevel("src/json/standardLevel/"+path);

            });
        }
    }

    private void chooseSavedGame() {
        ArrayList<Button> savedGames = this.menuView.getContentFrame().getListSavedGameButtons();
        Button savedLevel;

        for(int i=0; i<savedGames.size();i++){
            savedLevel = savedGames.get(i);

           final String path = (String) savedLevel.getUserData();
            savedLevel.setOnAction(e-> {
                this.controller.startLevel("src/json/savedLevel/"+path);
            });
        }
    }

}
