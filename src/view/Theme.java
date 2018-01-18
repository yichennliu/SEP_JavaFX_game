package view;
import javafx.scene.image.Image;
import model.enums.Token;
import model.themeEditor.SpriteSheet;
import model.themeEditor.ThemeEditor;
import model.themeEditor.HashMap2D;

import java.util.ArrayList;
import java.util.List;

public class Theme {

    public enum FeldType {ZEROEDGE, ONEEDGE, TWOEDGE, THREEEDGE, FOUREDGE,
                            STEP_SIDE,STEP_UP, STEP_DOWN, IDLE};
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

    public void addSpriteSheetForToken(Token token, FeldType feldType, SpriteSheet spriteSheet){
        List<SpriteSheet> variationImages = this.images.get(token,feldType);
        if(variationImages == null){
            variationImages = new ArrayList<SpriteSheet>();
            this.images.put(token,feldType,variationImages);
        }
        variationImages.add(spriteSheet);
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
