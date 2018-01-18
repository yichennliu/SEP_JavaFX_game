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

        Level level = LevelImporter.importLevel("json/levels/text.json") ;
        if(level!=null){
            GameView gameView = new GameView(this.view.getStage(),level);
            new GameController(level,gameView,this);
            this.view.update(View.Mode.GAME,gameView);
        }
        else {
            System.out.println("Nope! Level == null");
        }

    }

}
