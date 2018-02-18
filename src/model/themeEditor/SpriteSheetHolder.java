package model.themeEditor;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import model.themeEditor.Theme.Position;
import model.themeEditor.Theme.FeldType;

public class SpriteSheetHolder {

    private Map<Position,SpriteSheet> positions;
    private FeldType f;

    public SpriteSheetHolder(FeldType f){
        this.f = f;
        this.positions = new HashMap<>();
    }

    /* puts ( and if necessary, overwrites) spritesheet at given position */
    public void putSpriteSheet(Position p, SpriteSheet spriteSheet){
        if(isAllowed(p))
            this.positions.put(p,spriteSheet);
    }

    /* returns SpriteSheet for given Position or null */
    public SpriteSheet getSpriteSheet(Position p) {
        return this.positions.get(p);
    }

    /* checks if position is allowed for this Holder with its FeldType */
    private boolean isAllowed(Position p){
        for(Position allowed: f.getAllowedPositions()){
            if (allowed == p) return true;
        }
        return false;
    }

    /* returns Sprite at given index for provided position, null if position or index wrong */
    public Image getSprite(Theme.Position p, int frameIndex){
        if(this.positions.containsKey(p)){
            return (this.positions.get(p).getSprite(frameIndex));
        }
        return null;
    }

}
