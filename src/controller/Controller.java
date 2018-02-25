package controller;

import javafx.scene.image.Image;
import main.LevelFactory;
import model.enums.Medal;
import model.game.Level;
import model.game.MedalStatus;
import model.levelEditor.LevelEditor;
import model.misc.UsefulMethods;
import model.themeEditor.Theme;
import org.json.JSONObject;
import org.json.JSONTokener;
import view.*;
import view.levelEditor.LevelEditorView;
import view.themeEditor.ThemeEditorView;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.InputStream;
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
    private List<Theme> themeList;
    private int currentLevelIndex =0;


    public Controller(View view, Object model) { // Todo: ControllerModel
        Image icon = null;
        view.getStage().getIcons().add(new Image("main/icon.png"));
        this.view = view;
        this.currentMode = View.Mode.GAME;
        this.levelList = UsefulMethods.scanLevelDirectory();
        this.themeList = UsefulMethods.scanThemeDirectory();
      }


    public MenuController getMenuController() {
        return this.menuController;
    }

    public void startLevelEditor(){
        this.view.getStage().setTitle("Level-Editor");
        this.currentMode = View.Mode.EDITOR;
        LevelEditor levelEditor = new LevelEditor();
        LevelEditorView levelEditorView = new LevelEditorView(this.view.getStage(), levelEditor);
        LevelEditorController controller = new LevelEditorController(this, levelEditor, levelEditorView);
        this.view.update(View.Mode.EDITOR, levelEditorView);
    }

    public void startMenu(){
        this.currentMode = View.Mode.MENU;
        view.getStage().setTitle("Boulderdash");
        MenuView menuView = null;
        if (menuController == null) {
            Map<String, MedalStatus> medalStatusMap = this.importMedalStatuses();
            menuView = new MenuView(this.view.getStage(),medalStatusMap);
            menuController = new MenuController(menuView, medalStatusMap,this);
        } else {
            menuView = new MenuView(this.view.getStage(),menuController.getMedalStatuses());
            menuController.setMenuView(menuView);
            menuController.update();
        }
        this.view.update(View.Mode.MENU,menuView);
    }

    public void startThemeEditor(){
        this.view.getStage().setTitle("Theme-Editor");
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

    public List<Theme> getThemes() {
        return themeList;
    }
}


