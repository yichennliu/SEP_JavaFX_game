package model.themeEditor;

import javafx.scene.image.*;
import view.Theme;

import java.util.*;

import view.Theme.Position;

public class SpriteSheet {

    private Theme.FeldType f;
    private String path; // optional;
    private int spriteSize;
    private Image defaultSheet;
    private Map<Position, List<Image>> positions; // List als Liste der Frames;

    public SpriteSheet(Image defaultSheet, Theme.FeldType f, int spriteSize) {
        this.spriteSize = spriteSize;
        this.defaultSheet = defaultSheet;
        this.f = f;
        initImages(defaultSheet);
    }

    private void initImages(Image defaultSheet){
        positions = new HashMap<>();
        positions.put(Position.DEFAULT, spriteSheetToImageList(defaultSheet,this.spriteSize));
        calcPositions();
    }

    private List<Image> spriteSheetToImageList(Image spriteSheet,int spriteSize){
        int count = getNumberOfSprites(spriteSheet,spriteSize);
        List<Image> list = new ArrayList<Image>();
        for(int i = 0; i< count; i++){
            list.add(getSprite(i,spriteSheet,spriteSize));
        }
        return list;
    }

    /* calculates SpriteSheets for all positions of this feldType (TWOEDGE_CORNER, THREEEDGE */

    private void calcPositions(){
        /* für alle Feldtypes, die nicht bewegbar sind */
        if(!f.isMovable() && f!= Theme.FeldType.ZEROEDGE && f!=Theme.FeldType.FOUREDGE){
            /*ONEEDGE AND THREEDGE */
            if(f == Theme.FeldType.ONEEDGE || f == Theme.FeldType.THREEEDGE){
                Position[] positions = new Position[]{Position.TOP, Position.RIGHT, Position.BOTTOM, Position.LEFT};

                for(Position p: positions){
                    if (p==Position.TOP) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.RIGHT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,90),spriteSize));
                    if (p==Position.BOTTOM) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,180),spriteSize));
                    if (p==Position.LEFT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,270),spriteSize));
                }
            }
            /*TWOEDGE*/
            if(f==Theme.FeldType.TWOEDGE){
                Position[] positions = new Position[]{Position.TOP, Position.LEFT};
                for(Position p: positions){
                    if(p==Position.TOP) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.LEFT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,90),spriteSize));
                }
            }
            /*TWOEDGE_CORNER*/
            if(f==Theme.FeldType.TWOEDGE_CORNER) {
                Position[] positions = new Position[]{Position.TOPLEFT, Position.TOPRIGHT, Position.BOTTOMRIGHT, Position.BOTTOMLEFT};

                for(Position p: positions){
                    if(p==Position.TOPLEFT) this.positions.put(p,this.positions.get(Position.DEFAULT));
                    if (p==Position.TOPRIGHT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,90),spriteSize));
                    if (p==Position.BOTTOMRIGHT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,180),spriteSize));
                    if (p==Position.BOTTOMLEFT) this.positions.put(p,spriteSheetToImageList(getEachSpriteRotate(defaultSheet, spriteSize,270),spriteSize));
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
                    if (p==Position.LEFT) this.positions.put(p,spriteSheetToImageList(getEachSpriteMirrored(defaultSheet,spriteSize,true),spriteSize));
                }
            }
            if(f==Theme.FeldType.IDLE){

            }
        }
    }

    public void setSpriteSize(int spriteSize) throws Exception {
        if(spriteSize > (int) defaultSheet.getHeight() || spriteSize > (int) defaultSheet.getWidth() ||
                defaultSheet.getWidth() % spriteSize !=0.0 || defaultSheet.getHeight() % spriteSize!=0.0)
            throw new Exception("Sprite -size does not fit to height and/or width of Spritesheet");

        this.spriteSize = spriteSize;
        calcPositions();
    }

    public int getNumberOfSprites(Image spriteSheet,int spriteSize){
        return ( (int) (spriteSheet.getWidth() * spriteSheet.getHeight()) / (int) Math.pow(spriteSize,2));
    }

    // returns the default spriteSheet with rotated sprites
    private Image getEachSpriteRotate(Image spriteSheet,int spriteSize,int angle){
        WritableImage result = new WritableImage((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight());
        PixelWriter iw = result.getPixelWriter();
        for(int i=0; i<getNumberOfSprites(spriteSheet,spriteSize); i++){

            Image sourceSprite = ImageProcessor.getRotatedBy(angle,getSprite(i,spriteSheet,spriteSize));
            int colCount = ((int) spriteSheet.getWidth()) / spriteSize;
            int posY = (i / colCount) * spriteSize;
            int posX = (i % colCount) * spriteSize;
            iw.setPixels(posX,posY,spriteSize,spriteSize,sourceSprite.getPixelReader(),0,0);
        }
        return result;
    }

    //returns a SpriteSheet with mirrored sprites
    private Image getEachSpriteMirrored(Image spriteSheet,int spriteSize, boolean vertical){
        WritableImage result = new WritableImage((int) spriteSheet.getWidth(), (int) spriteSheet.getHeight());
        PixelWriter iw = result.getPixelWriter();
        for(int i=0; i<getNumberOfSprites(spriteSheet,spriteSize); i++){
            Image sourceSprite = ImageProcessor.getMirrored(vertical,getSprite(i,spriteSheet,spriteSize));
            int colCount = ((int) spriteSheet.getWidth()) / spriteSize;
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

    public Image getSprite(int index, Image spriteSheet, int spriteSize){
        int spriteCount = (int) spriteSheet.getWidth() * (int) spriteSheet.getHeight() / (int) Math.pow(spriteSize,2);
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


    /* returns Spritesheet for position or null if position not available or index to small or to high*/
    public Image getSprite(Position position, int index) {
        List<Image> list = this.positions.get(position);
        if (list!=null) return list.get(index);
        return null;

    }

    public Image getDefaultSheet(){
        return this.defaultSheet;
    }
}
