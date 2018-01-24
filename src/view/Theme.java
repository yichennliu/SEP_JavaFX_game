package view;
import javafx.scene.image.Image;
import model.enums.Token;
import model.themeEditor.SpriteSheet;
import model.themeEditor.ThemeEditor;
import model.themeEditor.HashMap2D;

import java.util.ArrayList;
import java.util.List;

public class Theme {

    public enum FeldType {
        ZEROEDGE(false), ONEEDGE(false), TWOEDGE(false), TWOEDGE_CORNER(false), THREEEDGE(false), FOUREDGE(false), // for "static" fields
        STEP_SIDE(true),STEP_UP(true), STEP_DOWN(true), IDLE(true); // for "moving" fields

        private boolean movable;
        FeldType(boolean movable){
            this.movable = movable;
        }
        public boolean isMovable(){
            return this.movable;
        }
    }

    public enum Position {TOP, BOTTOM, LEFT, RIGHT, TOPRIGHT,TOPLEFT,BOTTOMRIGHT, BOTTOMLEFT, DEFAULT};

    private HashMap2D<Token,FeldType,List<SpriteSheet>> images = new HashMap2D();
    private String name;

    public Theme(HashMap2D<Token,FeldType,List<SpriteSheet>> images, String name){
        this.name = name;
        this.images = images;
    }

    public SpriteSheet getSpriteSheet(Token token, FeldType feldType, int variationNumber) {
        List<SpriteSheet> imageList = this.images.get(token,feldType);
        if(imageList != null) return imageList.get(variationNumber);
        return null;
    }

    public String getName(){
        return name;
    }

    public HashMap2D<Token,FeldType,List<SpriteSheet>> getImages(){
        return this.images;
    }

    public int getNumberOfVariations(Token token, FeldType feldtype){
       List<SpriteSheet> imageList = this.images.get(token,feldtype);
       if (imageList!=null){
           return imageList.size();
       }
       else return 0;
    }
}
