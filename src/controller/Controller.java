package controller;

import main.LevelImporter;
import model.game.Level;
import model.themeEditor.ThemeEditor;
import view.*;

public class Controller {
    private View view;
    private View.Mode currentMode;

    private PrimaryController primaryController;
    private MenuController menuController;
    private ThemeEditorController themeEditorController;
    private GameController gameController;

    public Controller(View view, Object menuModel) { // Todo: MenuModel
        this.view = view;
        this.currentMode = View.Mode.GAME;
      }


    public void startPrimaryPage(){
        this.currentMode = View.Mode.PRIMARY;
        PrimaryPage primaryPage = new PrimaryPage(this.view.getStage());

        if (primaryController == null) {
            primaryController = new PrimaryController(primaryPage,this);
        } else {
            primaryController.setPrimaryPage(primaryPage);
        }

        this.view.update(View.Mode.PRIMARY,primaryPage);

    }

    public void startMenu(String playerName){
        this.currentMode = View.Mode.MENU;
        MenuView menuView = new MenuView(this.view.getStage(),null,playerName);

        if (menuController == null) {
            menuController = new MenuController(menuView,null,this);
        } else {
            menuController.setMenuView(menuView);
        }

        this.view.update(View.Mode.MENU,menuView);

    }

    public void startThemeEditor(){
        this.currentMode = View.Mode.THEME;

        ThemeEditor editor = new ThemeEditor();
        ThemeEditorView themeEditorView = new ThemeEditorView(this.view.getStage(),editor);

        if (themeEditorController == null) {
            themeEditorController = new ThemeEditorController(themeEditorView,editor,this);
        } else {
            themeEditorController.setThemeEditorView(themeEditorView);
        }

        this.view.update(View.Mode.THEME,themeEditorView);
    }

    public void startGame(){
        this.currentMode = View.Mode.GAME;
        Level level = LevelImporter.importLevel("json/level/text.json");

        if (gameController == null) {
            GameView gameView = new GameView(this.view.getStage(),level);

            gameController = new GameController(level,gameView,this);
        } else {
//            gameController.setGameView(gameView);
        }

        this.view.update(View.Mode.GAME, gameController.getGameView());
        gameController.tick();
    }

}
