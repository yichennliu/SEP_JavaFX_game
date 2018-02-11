package view.animation;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import view.Board;
import view.View;

import java.util.List;

public class BoardTranslationTransition extends Transition{

    private double translX, translY;
    private Affine transformationStart;
    private Affine transformation;
    private List<StaticViewToken> staticTokens;
    private boolean active;
    private boolean hasTranslate;
    private Board board;

    public BoardTranslationTransition(Board board){
        super();
        super.setCycleDuration(Duration.seconds(1.0/6.0));
        super.setInterpolator(Interpolator.EASE_BOTH);
        super.setCycleCount(1);

        this.transformation = board.getTransformation();
        this.board = board;

        this.setOnFinished( e -> {
            transformationStart = null;
            translY = 0;
            translX = 0;
            staticTokens = null;
            hasTranslate = false;
            active = false;
        });
    }

    public void setStaticViewTokens(List<StaticViewToken> list){
        this.staticTokens = list;
    }

    public void setTranslate(Translate translation){
        if(!hasTranslate){
            this.translX = translation.getX();
            this.translY = translation.getY();
            this.transformationStart = transformation.clone();
            hasTranslate = true;
            System.out.println("Translation set");
        }

    }

    private void updateGCs(Affine transF){
        board.getAnimationGC().setTransform(transF);
        board.getStaticGC().setTransform(transF);
        View.drawViewTokens(board,this.staticTokens);
    }

    public void interpolate(double frac){
        if(!active || staticTokens == null || !hasTranslate) return;
        Translate newTranslate = new Translate(translX*frac,translY*frac);
        transformation.setToTransform(transformationStart);
        transformation.prepend(newTranslate);
        updateGCs(transformation);
    }

    public void stop(){
        if(this.active){
            this.active = false;
        }
    }

    public void play(){
       if(hasTranslate){
           this.active = true;
           super.play();
       }

    }
}
