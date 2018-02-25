package model.themeEditor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageProcessor {

    /*rotates Image by 90, 180 or 270 degrees, specified by angle TODO: Enums*/
    public static Image getRotatedBy(int angle, Image img)    {
        if(angle!=90 && angle!=180 && angle!= 270) {
            return null;
        }
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();

        WritableImage resultImg;
        PixelWriter pw;
        switch(angle){
            case 90: {
                resultImg = new WritableImage(height,width);
                pw = resultImg.getPixelWriter();
                for(int y = 0; y<height; y++){
                    for(int x = 0;x<width; x++){
                        int newX = height - 1 - y;
                        int newY = x;
                        pw.setArgb(newX,newY,reader.getArgb(x,y));
                    }
                }
                return resultImg;
            }
            case 180: {
                resultImg = new WritableImage(width,height);
                pw = resultImg.getPixelWriter();
                for(int y = 0; y<height; y++){
                    for(int x = 0;x<width; x++){
                        int newX = width - 1 - x;
                        int newY = height -1 - y;
                        pw.setArgb(newX,newY,reader.getArgb(x,y));
                    }
                }
                return resultImg;
            }
            case 270: {
                resultImg = new WritableImage(height,width);
                pw = resultImg.getPixelWriter();
                for(int y = 0; y<height; y++){
                    for(int x = 0;x<width; x++){
                        int newX = y;
                        int newY = width - 1 -x;
                        pw.setArgb(newX,newY,reader.getArgb(x,y));
                    }
                }
                return resultImg;
            }
        }
        return null;
    }

    /* mirrors Image vertical or horizontal*/
    public static Image getMirrored(boolean vertical,Image img) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();

        WritableImage resultImg = new WritableImage(width, height);
        PixelWriter pw = resultImg.getPixelWriter();
        if (vertical) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int newY = height - 1 - y;
                    pw.setArgb(x,newY,reader.getArgb(x,y));
                }
            }
        } else {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int newX = width - 1 - x;
                    pw.setArgb(newX,y,reader.getArgb(x,y));
                }
            }
        }
        return resultImg;
    }
}
