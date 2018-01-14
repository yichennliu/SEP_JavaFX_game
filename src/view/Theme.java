package view;
import javafx.scene.image.Image;
import model.enums.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Theme {

    public enum status {RIGHTBORDER, LEFTBORDER, BOTTOMLEFTBORDER, BOTTOMRIGHTBORDER, DEFAULT };
    private Map<Token, Map<status, Image>> images = this.images = new HashMap<Token, Map<status,Image>>();


    public void setSpriteSheetForToken(){

    }
}
