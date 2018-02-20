package model.Menu;

import controller.GameController;
import controller.MenuController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.LevelFactory;
import model.game.Level;
import model.misc.LevelSnapshot;
import model.themeEditor.Theme;

import java.io.File;
import java.util.ArrayList;

public class SavedGameProgress {

    private GameController gameController;
    private MenuController meunuController;
    private Level level;

    public SavedGameProgress(Level level, GameController gameController, MenuController menuController){
        this.gameController = gameController;
        this.level = level;
        this.meunuController = menuController;

    }

    public String[] getListofGameJson(){
        File dir = new File("src/json/savegame");
        return dir.list();
    }

    public ArrayList<ImageView> getSavedGamesImages(){
        Theme theme = null;

        ArrayList<ImageView> savedGameImgaes = new ArrayList<>();
        for (String path : getListofGameJson()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/savegame/"+path));
            ImageView snapshotview = new ImageView(snapshot);
            savedGameImgaes.add(snapshotview);
        }

       return savedGameImgaes;
    }





}
