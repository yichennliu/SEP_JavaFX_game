package controller;

import main.LevelFactory;
import model.game.Level;
import model.game.MedalStatus;
import view.*;
import view.themeEditor.ThemeEditorView;

import java.util.HashMap;

public class Controller {
    private View view;
    private View.Mode currentMode;

    private MenuController menuController;
    private ThemeEditorController themeEditorController;
    private GameController gameController;

    public Controller(View view, Object menuModel) { // Todo: MenuModel
        this.view = view;
        this.currentMode = View.Mode.GAME;

      }

    public void startMenu(){
        this.currentMode = View.Mode.MENU;
        MenuView menuView = new MenuView(this.view.getStage(),null);

        if (menuController == null) {
            menuController = new MenuController(menuView, new HashMap<String, MedalStatus>(),this);
        } else {

            menuController.setMenuView(menuView);
        }

        this.view.update(View.Mode.MENU,menuView);
    }

    public void startThemeEditor(){
        this.currentMode = View.Mode.THEME;

        ThemeEditorView themeEditorView = new ThemeEditorView(this.view.getStage());

            themeEditorController = new ThemeEditorController(themeEditorView,this);

        this.view.update(View.Mode.THEME,themeEditorView);
    }

    public void startGame(){
        this.startLevel("json/level/spiegelgeist.json");
    }


    public void startLevel(String levelPath){
        this.currentMode = View.Mode.GAME;

        Level level = LevelFactory.importLevel(levelPath);

        GameView gameView = new GameView(this.view.getStage(),level);

        if(gameController == null){
            gameController = new GameController(level,gameView,this);

        } else{
            gameController.addInGameMenu();
            gameController.setGameView(gameView);
            gameController.setLevel(level);
            gameController.update();

        }

        this.view.update(View.Mode.GAME, gameController.getGameView());
        gameController.tick();

    }


    }


