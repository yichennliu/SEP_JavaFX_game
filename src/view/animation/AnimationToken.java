package view.animation;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import model.game.Feld;
import model.themeEditor.SpriteSheet;

public class AnimationToken {

    private int index = 0;
    private int frameIndex = 0;
    private SpriteSheet sheet;
    private Point2D from,to;
    private String name;

    public AnimationToken(SpriteSheet sheet,Feld from, Feld to, int startIndex){
        this.sheet = sheet;
        this.from = new Point2D(from.getColumn(),from.getRow());
        this.to = new Point2D(to.getColumn(),to.getRow());
        this.index = startIndex;
        if(sheet==null) this.name = to.getToken().name();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isMoving(){
        return (!from.equals(to));
    }

    public String getName(){
        return this.name;
    }

    public SpriteSheet getSpriteSheet() {
        return sheet;
    }

    public Image getNextImage(double frac){
        if(sheet!=null){

            if (tresholdReached(frac)){
                increaseIndex();
            }
            return sheet.getSprite(index);
        }
        return null;
    }

    private boolean tresholdReached(double frac){
        double fpt =this.sheet.getFpt();
        int x = (int) (fpt*frac);
        if (x!=frameIndex){
            frameIndex = x;
            return true;
        }
        return false;

    }

    private void increaseIndex(){
        index = (index<this.sheet.getCount()-1) ? index+1 : 0;
    }

    public Point2D getFrom(){
        return this.from;
    }

    public Point2D getTo(){
        return this.to;
    }


}
