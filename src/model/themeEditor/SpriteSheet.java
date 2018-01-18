package model.themeEditor;

import javafx.scene.image.*;

public class SpriteSheet {

    private Image  spriteSheet;
    private String path; // optional;
    private PixelReader pixels;
    private int spriteSize;
    private int imageWidth;
    private int imageHeight;

    public SpriteSheet(Image sheet, int spriteSize) {


        this.spriteSheet = sheet;
        this.spriteSize = spriteSize;
        this.imageHeight = (int) this.spriteSheet.getHeight();
        this.imageWidth = (int) this.spriteSheet.getWidth();
        this.pixels = this.spriteSheet.getPixelReader();
    }

    public void setSpriteSize(int spriteSize) throws Exception {
        if(spriteSize > (int) spriteSheet.getHeight() || spriteSize > (int) spriteSheet.getWidth() ||
                spriteSheet.getWidth() % spriteSize !=0.0 || spriteSheet.getHeight() % spriteSize!=0.0)
            throw new Exception("Sprite -size does not fit to height and/or width of Spritesheet");

        this.spriteSize = spriteSize;
    }

    public String getPath(){
        return this.path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public int getSpriteSize(){
        return this.spriteSize;
    }

    public Image getSprite(int index){
        if(index < 0 || index > (imageHeight*imageWidth)/spriteSize)
            return null;

        int colCount = this.imageWidth / this.spriteSize;
        int row = index / colCount;
        int col = index % colCount;
        int posY = row * spriteSize;
        int posX = col * spriteSize;
        System.out.println(posX + " " + posY + "index: " + index + " size: " + spriteSize);

        WritableImage image = new WritableImage(this.spriteSize, this.spriteSize);
        PixelWriter pixelWriter = image.getPixelWriter();

        try {
            pixelWriter.setPixels(0,0,spriteSize,spriteSize,pixels,posX, posY);
        }
        catch(Exception e ){
            return null;
        }
        return image;
    }
}
