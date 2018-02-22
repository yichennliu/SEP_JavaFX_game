package model.levelEditor;

import model.enums.Property;
import model.enums.Token;
import model.game.Feld;
import model.game.Level;
import model.game.Rule;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.levelEditor.LevelEditor.Mode.BRUSH;

public class LevelEditor {

    private int width = 25;
    private int height = 25;
    private int[] gemGoals;
    private int[] timeGoals;
    private Feld[][] map;
    private Level level;
    private Theme theme;
    private Token currentToken;
    private Map<Property,Integer> props;
    private List<Rule> pre,post;
    public enum Mode {BRUSH,SELECT};
    private Mode mode = BRUSH;

    public LevelEditor(){
        this.map = new Feld[height][width];

        try {
            this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        }
        catch(Exception e){
            this.theme = null;
            System.out.println("Could not load ThemeFile");
        }
        initMap();
        timeGoals = new int[3];
        gemGoals = new int[3];
        this.pre = new ArrayList<>();
        this.post = new ArrayList<>();
        this.props = new HashMap<>();
        this.level = new Level(null,map,gemGoals,timeGoals,pre,post,null,props,null);
    }

    public void setCurrentToken(Token t){
        this.currentToken = t;
    }

    public Token getCurrentToken(){
        return this.currentToken;
    }

    public Level getLevel() {
        return level;
    }

    public void setGemGoal(int i, int value){
        this.level.getGemGoals()[i] = value;
    }

    public void setTimeGoal(int i, int value){
        this.level.getTickGoals()[i] = value;
    }

    private void initMap(){
        for(int row = 0; row < height; row++){
            for(int col = 0; col<width; col++){
                Feld feld = new Feld(Token.MUD,col,row);
                map[row][col] = feld;
            }
        }
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }

    public Mode getMode(){
        return this.mode;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Feld[][] getMap() {
        return map;
    }

    public void setMap(Feld[][] map) {
        this.map = map;
        this.level.setMap(map);
    }

    public Theme getTheme() {
        return theme;
    }

    public void setName(String name) {
        this.level.setName(name);
    }
}
