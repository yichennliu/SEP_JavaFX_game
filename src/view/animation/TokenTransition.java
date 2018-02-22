package view.animation;

import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class TokenTransition extends javafx.animation.Transition {

    private Map<Point2D,AnimationToken> tokenMap;
    private double fieldSize;
    private boolean active;
    private GraphicsContext gc;

    private Map<Point2D,AnimationToken> newTokenMap;

    public void startAdding(){
        newTokenMap = new HashMap<>();
    }

    public void stopAdding(){
        tokenMap = newTokenMap;
        newTokenMap = null;
    }

    /*Adds and draws Token (with first frame)*/
    public void add(AnimationToken token){
        if(newTokenMap==null) return;
        Point2D newFrom = token.getFrom();
        Point2D newTo = token.getTo();
        AnimationToken oldToken = tokenMap.get(newFrom);
        if(oldToken!=null){
            if(oldToken.getSpriteSheet()== token.getSpriteSheet()){
                token.setIndex(oldToken.getIndex());
            }
        }
        Image image = token.getNextImage(0);

        double posX = token.getFrom().getX() * fieldSize;
        double posY = token.getFrom().getY() * fieldSize;

        if(image!=null){
            gc.drawImage(image,posX,posY,fieldSize,fieldSize);
        }
        else{
            System.out.println("No Image!!!!");
            gc.setFill(Color.WHITE);
            gc.fillText('■'+"",posX+fieldSize/2,posY+fieldSize/2);
            gc.setFill(Color.BLACK);
        }

        newTokenMap.put(newTo,token);
    }


    public TokenTransition(GraphicsContext gc, double fieldSize){
        super();
        this.gc = gc;
        this.fieldSize = fieldSize;
        this.tokenMap = new HashMap<>();
        this.active = true;
        setInterpolator(Interpolator.LINEAR);
        setCycleCount(1);
        setDuration(1.0/5.0);
    }

    public void setDuration(double seconds){
        super.setCycleDuration(Duration.seconds(seconds));
    }

    public void play(){
        this.active = true;
        super.play();
    }

    public void stop(){
        interpolate(0.99);
        this.active = false;
        super.stop();
    }

    public void interpolate(double frac){
        if(active){
            Affine normalTransform = gc.getTransform();
            gc.setTransform(new Affine());
            gc.clearRect(0,0,gc.getCanvas().getWidth(),gc.getCanvas().getHeight());
            gc.setTransform(normalTransform);

            for(Map.Entry<Point2D,AnimationToken> entry: tokenMap.entrySet()){
                AnimationToken elmnt = entry.getValue();
                Image image = elmnt.getNextImage(frac);

                double posX = elmnt.getFrom().getX() * fieldSize;
                double posY = elmnt.getFrom().getY() * fieldSize;

                if(elmnt.isMoving()){
                    double fromX = elmnt.getFrom().getX();
                    double toX = elmnt.getTo().getX();
                    double toY = elmnt.getTo().getY();
                    double fromY = elmnt.getFrom().getY();
                    posX = (fromX + (toX - fromX) * frac) * fieldSize;
                    posY = (fromY +(toY-fromY) * frac) * fieldSize;
                }


                if(image!=null){
                    gc.drawImage(image,posX,posY,fieldSize,fieldSize);
                }
                else{
                    System.out.println("No Image!!!!");
                    gc.setFill(Color.WHITE);
                    gc.fillText('■'+"",posX+fieldSize/2,posY+fieldSize/2);
                    gc.setFill(Color.BLACK);
                }

            }
        }

    }
}
