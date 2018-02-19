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

    /*this class manages the canvas on which the game is drawn. It starts and stops animations and wraps
    * all important view-information about the current Game together*/
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

    /*returns the BoardTranslationTransition which manages the animation of the boards' translation*/
    public BoardTranslationTransition getTranslationAnimator(){
        return this.translationAnimator;
    }

    /*returns the GraphicsContext on which all 'static', non-moving objects are drawn*/
    public GraphicsContext getStaticGC(){
        return this.staticGC;
    }

    /*returns the current Theme*/
    public Theme getTheme() {
        return theme;
    }

    /*stops all animations on both canvas*/
    public void stopAnimation(){
        if(animator!=null)
            animator.stop();
        if(translationAnimator!=null)
            translationAnimator.stop();
    }

    /*returns map that is used to draw*/
    public Feld[][] getMap() {
        return map;
    }

    /*clears both canvas*/
    public void clearBoard(){
       clearGraphicsContext(this.staticGC);
       clearGraphicsContext(this.animationGC);
    }

    /*clears only canvas for the 'static', non-moving objects*/
    public void clearStaticGC(){
        clearGraphicsContext(this.staticGC);
    }

    /*manages the clearing of a canvas specified by the provided GraphicsContext*/
    private void clearGraphicsContext(GraphicsContext gc){
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,getWidth(),getHeight());
        if (gc == this.staticGC) gc.fillRect(0,0,getWidth(),getHeight());
        gc.setTransform(transformation);
    }

    /*returns the Animator that animates the moving and animated objects*/
    public TokenTransition getAnimator() {
        return animator;
    }

    /*starts both the translation of the board and the moving/animating of objects*/
    public void playAnimation() {
        if (animator != null)
            animator.play();
        if (translationAnimator!=null){
            translationAnimator.play();
        }
    }

    /*sets a new translate for the board*/
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

    /*returns the transformation that is used to transform both canvas*/
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
