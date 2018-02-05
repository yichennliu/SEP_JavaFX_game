package view;

import javafx.animation.Interpolator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class Board {

    private Canvas staticCanvas;
    private Canvas animationCanvas;
    private double fieldSize;
    private TokenTransition animator;
    private Affine transformation;
    private GraphicsContext staticGC;
    private GraphicsContext animationGC;

    public Board(Canvas staticCanvas, Canvas animationCanvas, double fieldSize){
        this.staticCanvas = staticCanvas;
        this.animationCanvas = animationCanvas;
        this.transformation = new Affine();
        this.fieldSize =fieldSize;

        this.staticGC = staticCanvas.getGraphicsContext2D();
        this.animationGC = animationCanvas.getGraphicsContext2D();

        this.animator = new TokenTransition(this.animationGC, this.fieldSize);
        animationCanvas.widthProperty().bind(staticCanvas.widthProperty());
        animationCanvas.heightProperty().bind(staticCanvas.heightProperty());

    }

    public GraphicsContext getStaticGC(){
        return this.staticGC;
    }

    public void stopAnimation(){
        if(animator!=null)
            animator.stop();
    }

    public void clearBoard(){
       clearGraphicsContext(this.staticGC);
       clearGraphicsContext(this.animationGC);
    }

    private void clearGraphicsContext(GraphicsContext gc){
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,getWidth(),getHeight());
        if (gc == this.staticGC) gc.fillRect(0,0,getWidth(),getHeight());
        gc.setTransform(transformation);
    }

    public void setAnimator(TokenTransition animator) {
        this.animator = animator;
    }

    public void resetAnimator(){
        if(this.animator!=null){
            this.animator.stop();
        }
    }

    public TokenTransition getAnimator() {
        return animator;
    }

    public void playAnimation() {
        if (animator != null)
            animator.setInterpolator(Interpolator.LINEAR);
            animator.setCycleCount(1);
            animator.setDuration(1.0/5.0);
            animator.play();
    }

    public double getHeight(){
        return this.staticCanvas.getHeight();
    }

    public double getWidth(){
        return this.staticCanvas.getWidth();
    }

    public GraphicsContext getAnimationGC(){
        return this.animationGC;
    }

    public Affine getTransformation(){
        return this.transformation;
    }

    public void applyTransformation(Affine transformation){
        this.transformation = transformation;
        this.staticGC.setTransform(transformation);
        this.animationGC.setTransform(transformation);
    }

    public double getFieldSize() {
        return fieldSize;
    }
}
