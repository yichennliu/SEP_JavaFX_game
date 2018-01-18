package controller;

import main.LevelImporter;
import model.game.Level;
import model.themeEditor.ThemeEditor;
import view.*;

public class Controller {
    private View view;
    private View.Mode currentMode;

    public Controller(View view, Object menuModel) { // Todo: MenuModel
        this.view = view;
        this.currentMode = View.Mode.GAME;
      }


    public void startPrimaryPage(){
        this.currentMode = View.Mode.PRIMARY;
        PrimaryPage primaryPage = new PrimaryPage(this.view.getStage());
        new PrimaryController(primaryPage,this);
        this.view.update(View.Mode.PRIMARY,primaryPage);

    }

    public void startMenu(){
        this.currentMode = View.Mode.MENU;

        MenuView menuView = new MenuView(this.view.getStage(),null);
        new MenuController(menuView,null,this);

        this.view.update(View.Mode.MENU,menuView);
    }

    public void startThemeEditor(){
        this.currentMode = View.Mode.THEME;

        ThemeEditor editor = new ThemeEditor();
        ThemeEditorView themeEditorView = new ThemeEditorView(this.view.getStage(),editor);
        new ThemeEditorController(themeEditorView,editor,this);

        this.view.update(View.Mode.THEME,themeEditorView);
    }

    public void startGame(){
        this.currentMode = View.Mode.GAME;

        Level level = LevelImporter.importLevel("json/level/text.json") ;
        GameView gameView = new GameView(this.view.getStage(),level);
        new GameController(level,gameView,this);
        this.view.update(View.Mode.GAME,gameView);
    }

}
