package model.themeEditor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import model.themeEditor.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Image spriteSheet;
    private List<Image> sprites;
    int spriteSize, count;

    public SpriteSheet(Image spriteSheet, int spriteSize, int count) throws IllegalArgumentException{
        if(count> getMaxCount((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight(), spriteSize) ||
                sizeIsIllegal(spriteSize, spriteSheet)) {
            throw new IllegalArgumentException("count too high or spritesize illegal");
        }
        this.spriteSize = spriteSize;
        this.count = count;
        this.sprites = spriteSheetToImageList(spriteSheet,this.spriteSize,this.count);
        this.spriteSheet = spriteSheet;
    }

    /* refreshes SpriteSheet with all currently saved instance variables. Does nothing when illegal variables saved */
    private void refresh(){
        try {
            this.sprites = spriteSheetToImageList(this.spriteSheet,this.spriteSize,this.count);
        }
        catch(Exception e){

        }
    }

    /* tries to set spriteSize and to recalculate spriteList; throws IllegalArgumentException when spriteSize does not fit
    if currently saved count does not fit anymore maxCount of spriteSheet and the thumbnails spriteSize is taken */
    public void setSpriteSize(int spriteSize) throws IllegalArgumentException{
        if (sizeIsIllegal(spriteSize,this.spriteSheet)){
            throw new IllegalArgumentException("Illegal SpriteSize");
        }
        else {
            this.spriteSize = spriteSize;
            int maxCount =getMaxCount((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight(), spriteSize);
            if(this.count > maxCount) this.count = maxCount;
            refresh(); // refreshes sprites (in List)
        }
    }

    /* returns currently set spritecount */
    public int getCount(){
        return this.count;
    }

    /*sets count to provided value. Throws Exception with illegal argument*/
    public void setCount(int count) throws IllegalArgumentException{
        if(count<1 || count > getMaxCount((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight(), spriteSize)){
            throw new IllegalArgumentException("Too high or to low");
        }
        this.count = count;
        refresh();
    }

    /* return Sprite at given (frame)-index; null if index too high or too low*/
    public Image getSprite(int index){
        if(index>=0 && index<sprites.size()) return sprites.get(index);
        return null;
    }

    public int getSpriteSize() {
        return spriteSize;
    }

    /* returns whole spriteSheet */
    public Image getSpriteSheet(){
        return  this.spriteSheet;
    }

    /* checks if given spriteSize could be applied to given spritesheet*/
    public static boolean sizeIsIllegal(int spriteSize, Image spriteSheet){
        int height = (int) spriteSheet.getHeight();
        int width = (int) spriteSheet.getWidth();
        if(height % spriteSize!=0 || width % spriteSize!=0) return true;
        return false;
    }

    /* returns a copy of the Image where each sprite is mirrored (vertical/horizontal) returns null if illegal spriteSize provided*/
    public static Image getEachSpriteMirrored(Image spriteSheet,int spriteSize, boolean vertical){
        if(sizeIsIllegal(spriteSize,spriteSheet)) return null;
        WritableImage result = new WritableImage((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight());
        PixelWriter iw = result.getPixelWriter();
        int maxCount = getMaxCount((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight(),spriteSize);
        List<Image> sprites = spriteSheetToImageList(spriteSheet,spriteSize,maxCount);

        for(int i = 0; i< maxCount; i++){
            Image sourceSprite = ImageProcessor.getMirrored(vertical,sprites.get(i));
            int colCount = ((int) spriteSheet.getWidth()) / spriteSize;
            int posY = (i / colCount) * spriteSize;
            int posX = (i % colCount) * spriteSize;
            iw.setPixels(posX,posY,spriteSize,spriteSize,sourceSprite.getPixelReader(),0,0);
        }
        return result;

    }

    /* returns max count of sprites on spriteSheet with given spriteSize */
    private static int getMaxCount(int width, int height, int spriteSize){
        return width * height / (int) Math.pow(spriteSize,2);
    }

    /* returns a List of frames by extracting them from one spriteSheet. Assumes that every argument is right (!) */
    private static List<Image> spriteSheetToImageList(Image spriteSheet,int spriteSize, int count){
        List<Image> list = new ArrayList<Image>();
        for(int i = 0; i< count; i++){
            list.add(extractSprite(i,spriteSheet,spriteSize));
        }
        return list;
    }

    /* extracts Sprites from SpriteSheet with given spritesize and index, returns null when index too high or too low */
    private static Image extractSprite(int index, Image spriteSheet, int spriteSize){
        int spriteCount = getMaxCount((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight(), spriteSize);
        if(index < 0 || index > spriteCount){
            return null;
        }

        int colCount = (int) spriteSheet.getWidth() / spriteSize;
        int row = index / colCount;
        int col = index % colCount;
        int posY = row * spriteSize;
        int posX = col * spriteSize;

        WritableImage image = new WritableImage(spriteSize, spriteSize);
        PixelWriter pixelWriter = image.getPixelWriter();
        PixelReader reader = spriteSheet.getPixelReader();

        try {
            pixelWriter.setPixels(0,0,spriteSize,spriteSize,reader,posX, posY);
        }
        catch(Exception e ){
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        return image;
    }


}
