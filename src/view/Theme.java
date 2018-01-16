package view;
import javafx.scene.image.Image;
import model.enums.Token;
import model.themeEditor.ThemeEditor;
import model.themeEditor.HashMap2D;

import java.util.ArrayList;
import java.util.List;

public class Theme {

    public enum FeldType {DEFAULT, ONECORNER, TWOCORNER, LOONEY,
                            STEP_SIDE,STEP_UP, STEP_DOWN, IDLE};
    private HashMap2D<Token,FeldType,List<Image>> images = new HashMap2D();

    public Image getSpriteSheet(Token token, FeldType feldType, int variationNumber) {
        List<Image> imageList = this.images.get(token,feldType);
        if(imageList != null) return imageList.get(variationNumber);
        return null;
    }

    public void addSpriteSheetForToken(Token token, FeldType feldType, Image spriteSheet){
        List<Image> variationImages = this.images.get(token,feldType);
        if(variationImages == null){
            variationImages = new ArrayList<Image>();
            this.images.put(token,feldType,variationImages);
        }
        variationImages.add(spriteSheet);
    }

    public HashMap2D<Token,FeldType,List<Image>> getImages(){
        return this.images;
    }

    public int getNumberOfVariations(Token token, FeldType feldtype){
       List<Image> imageList = this.images.get(token,feldtype);
       if (imageList!=null){
           return imageList.size();
       }
       else return 0;
    }
}
