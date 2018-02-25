package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.enums.FieldDirection;
import model.enums.Property;
import model.enums.Token;
import model.game.Feld;
import model.themeEditor.SpriteSheet;
import model.themeEditor.Theme;
import model.themeEditor.Theme.FeldType;
import view.animation.AnimationToken;
import view.animation.BoardTranslationTransition;
import view.animation.StaticViewToken;
import view.animation.TokenTransition;
import view.levelEditor.LevelEditorView;
import view.themeEditor.ThemeEditorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View {
    private Stage stage;
    private Object currentScene;
    private Rectangle2D getwidthfullscreen;
    private double width;
    private double height;


    public enum Mode {EDITOR, GAME, MENU, THEME,PRIMARY};

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public View(Stage stage){
        this.stage = stage;
        this.getwidthfullscreen = Screen.getPrimary().getVisualBounds();
        this.width= getwidthfullscreen.getWidth();
        this.height= getwidthfullscreen.getHeight();
        this.stage.setWidth(width);
        this.stage.setHeight(height);
        this.stage.setResizable(false);
    }

    private void showMenu(){
        MenuView menuView = (MenuView) currentScene;
        stage.setScene(menuView.getSceneMenu());
    }

    public void showLevelEditor(){
        LevelEditorView levelEditorView = (LevelEditorView) currentScene;
        System.out.println("SHOW IT");
        stage.setScene(levelEditorView.getScene());
    }

    public void showGame(){
        GameView gameView = (GameView) currentScene;
        stage.setScene(gameView.getScene());
    }

    private void showTheme() {
        ThemeEditorView themeEditorView = (ThemeEditorView) currentScene;
        stage.setScene(themeEditorView.getScene());
    }

    public void update(Mode mode, Object scene) {
        this.currentScene = scene;
        switch (mode) {
            case GAME:
                    showGame();
                    break;
            case EDITOR:
                    showLevelEditor();
                    break;
            case MENU:
                    showMenu();
                    break;
            case THEME:
                    showTheme();
                    break;
        }
    }

    /*Draws board and starts animation on animationCanvas in Board*/
    public static void drawBoard(Board board, Feld[][] feld, Theme theme, boolean animate){
        board.clearBoard();
        TokenTransition animator = (animate) ? board.getAnimator() : null;
        BoardTranslationTransition translationAnimator = board.getTranslationAnimator();
        List<StaticViewToken> staticTokenList = (animate) ? new ArrayList<>() : null;
        double fieldSize = board.getFieldSize();

        if(animate) animator.startAdding();
        drawMap(feld,fieldSize,theme,animator, staticTokenList, board.getStaticGC());

        if(animate){
            animator.stopAdding();
            translationAnimator.setStaticViewTokens(staticTokenList);
            board.playAnimation();
        }
    }


    /*Draws 2D Feld-Array and fills Lists (if provided) of Animatable Items*/
    public static void drawMap(Feld[][] map, double fieldSize, Theme theme, TokenTransition animator, List<StaticViewToken> staticTokenList, GraphicsContext gc){
        int mapWidth = map[0].length;
        int mapHeight = map.length;

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum* fieldSize;
                double yPos = rowNum*fieldSize;
                Feld currentFeld =map[rowNum][colNum];

                Map<FieldDirection,Feld> neighbours = getEdgeNeighbours(currentFeld,true);
                Token t = currentFeld.getToken();
                FeldType f = getFeldType(currentFeld,neighbours);
                Theme.Position p = getFeldPosition(currentFeld,f,neighbours);
                SpriteSheet s = null;
                if(theme!=null) s = retrieveSpriteSheet(t,f,p,theme);

                if(animator!=null){
                    AnimationToken animationToken = getAnimation(currentFeld,s);
                    if(animationToken!=null){
                        animator.add(animationToken);
                        continue;
                    }
                }
                if(theme == null || !drawFeld(xPos, yPos,fieldSize,gc,s,theme)) {
                    drawTextFeld(map[rowNum][colNum],gc,xPos,yPos,fieldSize);
                }
                if(staticTokenList!=null)
                    staticTokenList.add(new StaticViewToken(xPos,yPos,map[rowNum][colNum].getToken().name(),s));
            }
        }
    }

    private static void drawTextFeld(String text, GraphicsContext gc, double xPos, double yPos, double fieldSize){
        gc.setFill(Color.WHITE);
        gc.fillText(text.equals("PATH") ? "" : (text.equals("ME") ? "■" : text.charAt(0)+""),
                xPos+fieldSize/2-7,yPos+fieldSize/2+5);
        gc.setFill(Color.BLACK);
    }

    public static void drawTextFeld(Feld feld, GraphicsContext gc, double xPos, double yPos, double fieldSize){
        String text = feld.toString();
        drawTextFeld(text, gc, xPos, yPos,fieldSize);
    }


    public static void drawViewTokens(Board board, List<StaticViewToken> list){
        board.clearStaticGC();
        for(StaticViewToken elmnt: list){
            double x = elmnt.getX();
            double y = elmnt.getY();
            if(!drawFeld(x,y,board.getFieldSize(),board.getStaticGC(),elmnt.getS(),board.getTheme())){
                drawTextFeld(elmnt.getName(),board.getStaticGC(),x,y,board.getFieldSize());
            }
        }
    }

    private static AnimationToken getAnimation(Feld feld, SpriteSheet sheet){
       boolean moved= feld.getCurrentTokenCameFrom() != null;
       boolean hasFrames = (sheet!=null) ? sheet.getCount()>1 : false;

       Feld from = feld;
       Feld to = feld;
       SpriteSheet s = null;


       if(!hasFrames && !moved) return null;

       if(moved){
           from = feld.getCurrentTokenCameFrom();
           if(!hasFrames) s = sheet;
       }

       if(hasFrames ){
           s = sheet;
       }

       return new AnimationToken(s,from,to,0);

    }

    /*returns true if a sprite could be drawn (also when not the requested but default/idle position was available)*/
    private static boolean drawFeld(double x, double y, double fieldSize, GraphicsContext gc, SpriteSheet s, Theme theme){

        if (s==null) return false;
        drawImage(x,y,fieldSize,gc,s.getSprite(0));
        return true;
    }

    public static void drawImage(double x, double y, double size, GraphicsContext gc, Image image){
        gc.drawImage(image,x,y,size,size);
    }

    /*tries to get spritesheet (with given position, feldtype, and token) from theme. If not avaible, tries to get Default
    * sheets for feldtype. returns null if no default spritesheet available*/
    private static SpriteSheet retrieveSpriteSheet(Token t, FeldType f, Theme.Position p, Theme theme){
        SpriteSheet s = theme.getSpriteSheet(t,f,p);
        if (s==null){

            s = t.isMovable()
                    ? theme.getSpriteSheet(t,Theme.FeldType.IDLE, Theme.Position.DEFAULT)
                    : theme.getSpriteSheet(t, FeldType.FOUREDGE, Theme.Position.DEFAULT);
        }

        return s;
    }

    private static Theme.FeldType getFeldType(Feld feld, Map<FieldDirection,Feld> neighbours){
        if(feld.getToken().isMovable()){
            boolean moved = feld.getPropertyValue(Property.MOVED).equals(1);
            if(moved){
                int direction = feld.getPropertyValue(Property.DIRECTION);
                if(direction>=1 && direction <=4) {
                    FieldDirection fdirection = FieldDirection.getFromDirection(direction);
                    switch(fdirection){
                        case TOP: return FeldType.STEP_UP;
                        case BOTTOM: return FeldType.STEP_DOWN;
                        default: return FeldType.STEP_SIDE;
                    }
                }
            }
            return FeldType.IDLE;
        }
        int neighbourCount = neighbours.size();
        switch(neighbourCount){
            case 0: return FeldType.ZEROEDGE;
            case 1: return FeldType.ONEEDGE;
            case 2:
                if(neighbours.get(FieldDirection.LEFT)!=null && neighbours.get(FieldDirection.RIGHT)!=null ||
                        neighbours.get(FieldDirection.TOP)!=null && neighbours.get(FieldDirection.BOTTOM)!=null){
                    return FeldType.TWOEDGE;
                }
                else return FeldType.TWOEDGE_CORNER;
            case 3: return FeldType.THREEEDGE;
            default: return FeldType.FOUREDGE;
        }
    }

    private static Map<FieldDirection,Feld> getEdgeNeighbours(Feld feld, boolean sameToken) {
        Map<FieldDirection, Feld> neighbours = new HashMap<FieldDirection, Feld>();
        Token token = feld.getToken();
        for (FieldDirection n : FieldDirection.values()) { // nur die direkten Nachbarn (ohne schräg oben/unten, etc.)
            if (n != FieldDirection.LEFTBOTTOM && n != FieldDirection.LEFTTOP && n != FieldDirection.RIGHTBOTTOM && n != FieldDirection.RIGHTTOP) {
                Feld neighbour = feld.getNeighbour(n);
                if (sameToken && (neighbour != null && neighbour.getToken() != token)) continue;
                if(neighbour!=null) neighbours.put(n, neighbour);
            }
        }
        return neighbours;
    }

    private static Theme.Position getFeldPosition (Feld feld, FeldType type, Map<FieldDirection,Feld> neighbours) {
        if (type == FeldType.FOUREDGE || type == FeldType.ZEROEDGE || type == FeldType.IDLE || type == FeldType.STEP_DOWN || type == FeldType.STEP_UP)
            return Theme.Position.DEFAULT;
        switch (type) {
            case ONEEDGE:
                if (neighbours.containsKey(FieldDirection.LEFT)) return Theme.Position.RIGHT;
                if (neighbours.containsKey(FieldDirection.RIGHT)) return Theme.Position.LEFT;
                if (neighbours.containsKey(FieldDirection.BOTTOM)) return Theme.Position.TOP;
                if (neighbours.containsKey(FieldDirection.TOP)) return Theme.Position.BOTTOM;
            case TWOEDGE:
                if (neighbours.containsKey(FieldDirection.LEFT) && neighbours.containsKey(FieldDirection.RIGHT))
                    return Theme.Position.HORIZONTAL;
                else return Theme.Position.VERTICAL;
            case TWOEDGE_CORNER:
                if (neighbours.containsKey(FieldDirection.TOP) && neighbours.containsKey(FieldDirection.RIGHT))
                    return Theme.Position.BOTTOMLEFT;
                if (neighbours.containsKey(FieldDirection.TOP) && neighbours.containsKey(FieldDirection.LEFT))
                    return Theme.Position.BOTTOMRIGHT;
                if (neighbours.containsKey(FieldDirection.BOTTOM) && neighbours.containsKey(FieldDirection.LEFT))
                    return Theme.Position.TOPRIGHT;
                if (neighbours.containsKey(FieldDirection.BOTTOM) && neighbours.containsKey(FieldDirection.RIGHT))
                    return Theme.Position.TOPLEFT;
            case THREEEDGE:
                if (!neighbours.containsKey(FieldDirection.TOP)) return Theme.Position.TOP;
                if (!neighbours.containsKey(FieldDirection.BOTTOM)) return Theme.Position.BOTTOM;
                if (!neighbours.containsKey(FieldDirection.RIGHT)) return Theme.Position.RIGHT;
                if (!neighbours.containsKey(FieldDirection.LEFT)) return Theme.Position.LEFT;
            case STEP_SIDE:
                FieldDirection fd = FieldDirection.getFromDirection(feld.getPropertyValue(Property.DIRECTION));
                if(fd.equals(FieldDirection.LEFT)) return Theme.Position.LEFT;
                else return Theme.Position.RIGHT;
        }
        return null;

    }

    public Stage getStage(){
        return this.stage;
    }



}
