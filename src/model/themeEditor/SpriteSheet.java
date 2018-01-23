package model.themeEditor;

import javafx.geometry.Pos;
import javafx.scene.image.*;
import view.Theme;

import java.util.HashMap;
import java.util.Map;
import model.themeEditor.ImageProcessor;
import view.Theme.Position;

import javax.imageio.ImageWriter;

public class SpriteSheet {

    private Theme.FeldType f;
    private String path; // optional;
    private int spriteSize;
    private int imageWidth;
    private int imageHeight;
    private Map<Position, Image> positions;
    private Map<Position, PixelReader> pixelReaders;

    public SpriteSheet(Image defaultSheet, Theme.FeldType f, int spriteSize) {
        this.spriteSize = spriteSize;
        this.imageHeight = (int) defaultSheet.getHeight();
        this.imageWidth = (int) defaultSheet.getWidth();
        this.f = f;

        initImages(defaultSheet);

    }

    private void initImages(Image defaultSheet){
        positions = new HashMap<Position, Image>();
        pixelReaders = new HashMap<Position,PixelReader>();
        positions.put(Position.DEFAULT,defaultSheet);
        pixelReaders.put(Position.DEFAULT,defaultSheet.getPixelReader());
        calcPositions();
    }

    /* calculates SpriteSheets for all positions of this feldType (TWOEDGE_CORNER, THREEEDGE
     */
    private void calcPositions(){
        /* für alle Feldtypes, die nicht bewegbar sind */
        if(!f.isMovable() && f!= Theme.FeldType.ZEROEDGE && f!=Theme.FeldType.FOUREDGE){
            /*ONEEDGE AND THREEDGE */
            if(f == Theme.FeldType.ONEEDGE || f == Theme.FeldType.THREEEDGE){
                System.out.println("calcing positions for " + f.name());
                Position[] positions = new Position[]{Position.TOP, Position.RIGHT, Position.BOTTOM, Position.LEFT};

                for(Position p: positions){
                    if (p==Position.TOP) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.RIGHT) this.positions.put(p,getEachSpriteRotate(90));
                    if (p==Position.BOTTOM) this.positions.put(p,getEachSpriteRotate(180));
                    if (p==Position.LEFT) this.positions.put(p,getEachSpriteRotate(270));
                }
            }
            /*TWOEDGE*/
            if(f==Theme.FeldType.TWOEDGE){
                Position[] positions = new Position[]{Position.TOP, Position.LEFT};
                for(Position p: positions){
                    if(p==Position.TOP) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.LEFT) this.positions.put(p,getEachSpriteRotate(90));
                }
            }
            /*TWOEDGE_CORNER*/
            if(f==Theme.FeldType.TWOEDGE_CORNER) {
                Position[] positions = new Position[]{Position.TOPLEFT, Position.TOPRIGHT, Position.BOTTOMRIGHT, Position.BOTTOMLEFT};

                for(Position p: positions){
                    if(p==Position.TOPLEFT) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.TOPRIGHT) this.positions.put(p,getEachSpriteRotate(90));
                    if (p==Position.BOTTOMRIGHT) this.positions.put(p,getEachSpriteRotate(180));
                    if (p==Position.BOTTOMLEFT) this.positions.put(p,getEachSpriteRotate(270));
                }
            }
        }
        /* für alle bewegbaren Feldtpyes */
        else {
            /* für Step-Side*/
            if(f==Theme.FeldType.STEP_SIDE){
                Position[] positions = new Position[]{Position.RIGHT, Position.LEFT};
                for(Position p: positions){
                    if(p==Position.RIGHT) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.LEFT) this.positions.put(p,getEachSpriteMirrored(true));
                }
            }
        }
    }

    public void setSpriteSize(int spriteSize) throws Exception {
        Image defaultImage = positions.get(Position.DEFAULT);
        if(spriteSize > (int) defaultImage.getHeight() || spriteSize > (int) defaultImage.getWidth() ||
                defaultImage.getWidth() % spriteSize !=0.0 || defaultImage.getHeight() % spriteSize!=0.0)
            throw new Exception("Sprite -size does not fit to height and/or width of Spritesheet");

        this.spriteSize = spriteSize;
        calcPositions();
    }

    public int getNumberOfSprites(){
        return (this.imageWidth * this.imageHeight / (int) Math.pow(this.spriteSize,2));
    }

    private Image getEachSpriteRotate(int angle){
        WritableImage result = new WritableImage(this.imageWidth, this.imageHeight);
        PixelWriter iw = result.getPixelWriter();
        for(int i=0; i<this.getNumberOfSprites(); i++){

            Image sourceSprite = ImageProcessor.getRotatedBy(angle,this.getSprite(Position.DEFAULT,i).getPixelReader(),spriteSize,spriteSize);
            int colCount = this.imageWidth / this.spriteSize;
            int posY = (i / colCount) * spriteSize;
            int posX = (i % colCount) * spriteSize;
            iw.setPixels(posX,posY,spriteSize,spriteSize,sourceSprite.getPixelReader(),0,0);
        }
        return result;
    }

    private Image getEachSpriteMirrored(boolean vertical){
        WritableImage result = new WritableImage(this.imageWidth, this.imageHeight);
        PixelWriter iw = result.getPixelWriter();
        for(int i=0; i<this.getNumberOfSprites(); i++){
            Image sourceSprite = ImageProcessor.getMirrored(vertical,this.getSprite(Position.DEFAULT,i).getPixelReader(),spriteSize,spriteSize);
            int colCount = this.imageWidth / this.spriteSize;
            int posY = (i / colCount) * spriteSize;
            int posX = (i % colCount) * spriteSize;
            iw.setPixels(posX,posY,spriteSize,spriteSize,sourceSprite.getPixelReader(),0,0);
        }
        return result;
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

    /* returns Spritesheet for position or null if position not available or index to small or to high*/
    public Image getSprite(Position position, int index){
        if(index < 0 || index > (getNumberOfSprites()) || !positions.containsKey(position)){
            System.out.println("Sprite not found or index to high: i:" + index + " position: " + position.name());
            return null;
        }

        int colCount = this.imageWidth / this.spriteSize;
        int row = index / colCount;
        int col = index % colCount;
        int posY = row * spriteSize;
        int posX = col * spriteSize;

        WritableImage image = new WritableImage(this.spriteSize, this.spriteSize);
        PixelWriter pixelWriter = image.getPixelWriter();

        boolean posAvailable = positions.containsKey(position);

        Image img = positions.get(position);
        PixelReader reader = pixelReaders.get(position);


        try {
            pixelWriter.setPixels(0,0,spriteSize,spriteSize,reader,posX, posY);
        }
        catch(Exception e ){
            System.out.println(e.getMessage());
            return null;
        }
        return image;
    }

    public Image getSpriteSheet(Position position){
        if(!positions.containsKey(position)) return null;
        return positions.get(position);
    }
}
