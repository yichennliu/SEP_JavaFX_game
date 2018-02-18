package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import model.game.Feld;
import model.themeEditor.Theme;
import view.animation.BoardTranslationTransition;
import view.animation.TokenTransition;

public class Board {

    private Canvas staticCanvas;
    private Canvas animationCanvas;
    private double fieldSize;
    private TokenTransition animator;
    private BoardTranslationTransition translationAnimator;
    private Affine transformation;
    private GraphicsContext staticGC;
    private GraphicsContext animationGC;
    private Feld[][] map;
    private Theme theme;

    public Board(Canvas staticCanvas, Canvas animationCanvas, Feld[][] map, Theme theme, double fieldSize){
        this.staticCanvas = staticCanvas;
        this.animationCanvas = animationCanvas;
        this.transformation = new Affine();
        this.fieldSize =fieldSize;
        this.map = map;
        this.theme = theme;

        this.staticGC = staticCanvas.getGraphicsContext2D();
        this.animationGC = animationCanvas.getGraphicsContext2D();

        this.animator = new TokenTransition(this.animationGC, this.fieldSize);
        this.translationAnimator = new BoardTranslationTransition(this);
        animationCanvas.widthProperty().bind(staticCanvas.widthProperty());
        animationCanvas.heightProperty().bind(staticCanvas.heightProperty());
    }

    public BoardTranslationTransition getTranslationAnimator(){
        return this.translationAnimator;
    }

    public GraphicsContext getStaticGC(){
        return this.staticGC;
    }

    public Theme getTheme() {
        return theme;
    }

    public void stopAnimation(){
        if(animator!=null)
            animator.stop();
        if(translationAnimator!=null)
            translationAnimator.stop();
    }

    public Feld[][] getMap() {
        return map;
    }

    public void clearBoard(){
       clearGraphicsContext(this.staticGC);
       clearGraphicsContext(this.animationGC);
    }

    public void clearStaticGC(){
        clearGraphicsContext(this.staticGC);
    }

    private void clearGraphicsContext(GraphicsContext gc){
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,getWidth(),getHeight());
        if (gc == this.staticGC) gc.fillRect(0,0,getWidth(),getHeight());
        gc.setTransform(transformation);
    }

    public void resetAnimator(){
        stopAnimation();
    }

    public TokenTransition getAnimator() {
        return animator;
    }

    public void playAnimation() {
        if (animator != null)
            animator.play();
        if (translationAnimator!=null){
            translationAnimator.play();
        }
    }

    public void translate(Translate translation){
        translationAnimator.setTranslate(translation);
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
