package controller;

import javafx.scene.media.AudioClip;
import main.LevelFactory;
import model.enums.Medal;
import model.game.Level;
import model.game.MedalStatus;
import model.levelEditor.LevelEditor;
import model.misc.UsefulMethods;
import org.json.JSONObject;
import org.json.JSONTokener;
import view.*;
import view.levelEditor.LevelEditorView;
import view.themeEditor.ThemeEditorView;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private View view;
    private View.Mode currentMode;

    private MenuController menuController;
    private ThemeEditorController themeEditorController;
    private GameController gameController;
    private List<String> levelList;
    private int currentLevelIndex =0;


    public Controller(View view, Object model) { // Todo: ControllerModel
        this.view = view;
        this.currentMode = View.Mode.GAME;
        this.levelList = UsefulMethods.scanLevelDirectory();
      }


    public MenuController getMenuController() {
        return this.menuController;
    }

    public void startLevelEditor(){
        this.currentMode = View.Mode.EDITOR;
        LevelEditor levelEditor = new LevelEditor();
        LevelEditorView levelEditorView = new LevelEditorView(this.view.getStage(), levelEditor);
        LevelEditorController controller = new LevelEditorController(this, levelEditor, levelEditorView);
        this.view.update(View.Mode.EDITOR, levelEditorView);
    }

    public void startMenu(){
        this.currentMode = View.Mode.MENU;
        Map<String, MedalStatus> medalStatusMap = this.importMedalStatuses();
        MenuView menuView = new MenuView(this.view.getStage(),medalStatusMap);

        if (menuController == null) {
            menuController = new MenuController(menuView, medalStatusMap,this);
        } else {
            menuController.setMenuView(menuView);
            menuController.update();

        }

        this.view.update(View.Mode.MENU,menuView);
    }

    public void startThemeEditor(){
        this.currentMode = View.Mode.THEME;

        ThemeEditorView themeEditorView = new ThemeEditorView(this.view.getStage());

        this.themeEditorController = new ThemeEditorController(themeEditorView,this);

        this.view.update(View.Mode.THEME,themeEditorView);
    }

    public void startGame(){
        this.startLevel("src/json/level/"+levelList.get(currentLevelIndex));
    }


    public void startLevel(String levelPath){
        this.currentMode = View.Mode.GAME;

        Level level = LevelFactory.importLevel(levelPath);

        GameView gameView = new GameView(this.view.getStage(),level);

        if(gameController == null){
            gameController = new GameController(level,gameView,this);

        } else{

            gameController.addEscapeGameMenu();
            gameController.setGameView(gameView);
            gameController.setLevel(level);
            gameController.update();

        }

        this.view.update(View.Mode.GAME, gameController.getGameView());
        gameController.tick();

    }


    public void startNextLevel(){
        currentLevelIndex = (currentLevelIndex==levelList.size()-1) ? 0 : currentLevelIndex+1;
        this.startLevel("src/json/level/"+levelList.get(currentLevelIndex));

    }

    /**
     * @return saved Medal statuses, an empty map otherwise
     */
    private Map<String, MedalStatus> importMedalStatuses() {
        Map<String, MedalStatus> medalStatuses = new HashMap<>();
        InputStream is = ClassLoader.getSystemResourceAsStream("json/medals/medalstatuses.json");

        if (is != null) {
            JSONObject jsonMedalStatuses = new JSONObject(new JSONTokener(is));

            for (String path : jsonMedalStatuses.keySet()) {
                JSONObject jsonMedalStatus = jsonMedalStatuses.getJSONObject(path);
                MedalStatus medalStatus = new MedalStatus();

                if (jsonMedalStatus.getBoolean("bronze"))
                    medalStatus.set(Medal.BRONZE);
                if (jsonMedalStatus.getBoolean("silver"))
                    medalStatus.set(Medal.SILVER);
                if (jsonMedalStatus.getBoolean("gold"))
                    medalStatus.set(Medal.GOLD);

                medalStatuses.put(path, medalStatus);
            }
        }
        return medalStatuses;
    }
}


