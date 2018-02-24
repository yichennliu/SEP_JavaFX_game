package controller;


import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import model.game.Level;
import model.game.MedalStatus;
import view.LevelItem;
import view.MenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuController {

    private MenuView menuView;
    private Map<String, MedalStatus> medalStatuses;
    private List<Level> levelList;
    private Controller controller;


    public MenuController(MenuView menuView, Map<String, MedalStatus> medalStatuses, Controller controller) {
        this.menuView = menuView;
        this.medalStatuses = medalStatuses;
        this.controller = controller;
        this.addMenuViewComponents();
        this.chooseLevel();
        this.chooseSavedGame();

    }

    public void update() {
        this.chooseLevel();
        this.chooseSavedGame();
    }

    /**
     * @return Status der Medaillen. Key ist der Pfad des Levels (level.getJsonPath()),
     * Value ist ein Objekt in dem steht, welche Medaillen für dieses Level erreicht wurden
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

        menuView.getContentFrame().getClose().setOnAction(e ->{
            Platform.exit();
        });

    }

    private void chooseLevel() {
        ArrayList<LevelItem> levelButtons = menuView.getContentFrame().getListLevelButtons();
        LevelItem level;

        for (int i = 0; i < levelButtons.size(); i++) {
            level = levelButtons.get(i);

            final String path = (String) level.getUserData();
            level.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                this.controller.startLevel("src/json/level/" + path);
            });

        }
    }

    private void chooseSavedGame() {
        ArrayList<LevelItem> savedGames = menuView.getContentFrame().getListSavedGameButtons();
        LevelItem savedLevel;

        for (int i = 0; i < savedGames.size(); i++) {
            savedLevel = savedGames.get(i);

            final String path = (String) savedLevel.getUserData();
            savedLevel.addEventHandler(MouseEvent.MOUSE_CLICKED, ev ->{
                this.controller.startLevel("src/json/savegame/" + path);
            });
        }
    }


}
