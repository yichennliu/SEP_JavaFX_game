package controller;

import main.LevelFactory;
import model.game.Level;
import view.*;
import view.themeEditor.ThemeEditorView;

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

    public void start(View.Mode mode){
        this.currentMode = mode;



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

    public void startMenu(){
        this.currentMode = View.Mode.MENU;
        MenuView menuView = new MenuView(this.view.getStage(),null);

        if (menuController == null) {
            menuController = new MenuController(menuView,null,this);
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
        this.startLevel("json/level/text.json");
    }


    public void startLevel(String levelPath){
        this.currentMode = View.Mode.GAME;
        Level level = LevelFactory.importLevel(levelPath);
        GameView gameView = new GameView(this.view.getStage(),level);
        gameController = new GameController(level,gameView,this);

        this.view.update(View.Mode.GAME, gameController.getGameView());
        gameController.tick();

    }

}
