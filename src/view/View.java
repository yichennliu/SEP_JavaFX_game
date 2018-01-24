package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import model.enums.FieldDirection;
import model.enums.Token;
import model.game.Feld;
import model.game.Level;
import model.themeEditor.SpriteSheet;
import view.Theme.FeldType;

import java.util.HashMap;
import java.util.Map;

public class View {

    private GraphicsContext gameGC;
    private GraphicsContext  editorGC;

    private Stage stage;
    private Object currentScene;

    private Level level;
    private int maxSize = 1000;

    private double windowWidth = 800;
    private double windowHeight = 600;
    private double menuWidth= 500;
    private double menuHeight= 400;



    private double fieldSize = 20;

    public enum Mode {EDITOR, GAME, MENU, THEME,PRIMARY};

    public View(Stage stage){
        this.stage = stage;
        this.stage.setWidth(windowWidth);
        this.stage.setHeight(windowHeight);
        this.stage.centerOnScreen();
    }

    private void showMenu(){
        MenuView menuView = (MenuView) currentScene;
        stage.setScene(menuView.getSceneMenu());
    }

//    private void showEditor(){
//        stage.setScene(this.levelEditor);
//    }

    public void showGame(){
        GameView gameView = (GameView) currentScene;
        stage.setScene(gameView.getScene());
    }

    private void showTheme() {
        ThemeEditorView themeEditorView = (ThemeEditorView) currentScene;
        stage.setScene(themeEditorView.getSceneThemeView());
    }

    private void showPrimary(){

        PrimaryPage primaryPage= (PrimaryPage) currentScene;
        stage.setScene(primaryPage.getScene());

    }


    public void update(Mode mode, Object scene) {
        this.currentScene = scene;
        switch (mode) {
            case PRIMARY:
                showPrimary();

                break;

            case GAME:
                    showGame();
//                    Canvas gameCanvas = gameScene.getCanvas();
//                    drawMap(gameCanvas,gameCanvas.getGraphicsContext2D());
                    break;
/*            case EDITOR:
                    showEditor();
                    break;*/
            case MENU:
                    showMenu();
                    break;
            case THEME:
                    showTheme();
                    break;
        }
    }


    public static void drawMap(GraphicsContext gc, Feld[][] feld, double fieldSize, Theme theme){
        Canvas canvas = gc.getCanvas();

        Affine actualTransformation = gc.getTransform();
        Affine defaultTransform = new Affine();
        gc.setTransform(defaultTransform);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setTransform(actualTransformation);

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        int mapWidth = feld[0].length;
        int mapHeight = feld.length;

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum* fieldSize;
                double yPos = rowNum*fieldSize;
                if(theme == null || !drawFeld(xPos, yPos, gc, feld[rowNum][colNum], fieldSize, theme)) {
                    String text = feld[rowNum][colNum].toString();
                    // Path verstecken, sonst ersten Buchstaben anzeigen
                    gc.setFill(Color.WHITE);
                    gc.fillText(text.equals("PATH") ? "" : (text.equals("ME") ? "ME" : text.charAt(0)+""),
                            xPos+fieldSize/2-7,yPos+fieldSize/2+5);
                    gc.setFill(Color.BLACK);
                }
            }
        }
    }

    private static boolean drawFeld(double x, double y, GraphicsContext gc, Feld feld, double fieldSize, Theme theme){
        Map<FieldDirection,Feld> neighbours = getEdgeNeighbours(feld,true);
        Token t = feld.getToken();
        FeldType f = getFeldType(feld,neighbours);
        Theme.Position p = getFeldPosition(feld,f,neighbours);
        Image sprite;
        SpriteSheet s = theme.getSpriteSheet(t,f,0);

        if(s==null){
            s = (f.isMovable())? theme.getSpriteSheet(t,FeldType.IDLE,0) : theme.getSpriteSheet(t,FeldType.FOUREDGE,0);
            if (s==null) {
                return false;
            }
            sprite = s.getSprite(p,0);
            if(sprite == null){
                sprite = s.getSprite(Theme.Position.DEFAULT,0);
            }

        }
        else {
            sprite = s.getSprite(p,0);
            if (sprite== null) sprite = s.getSprite(Theme.Position.DEFAULT,0);
        }
        gc.drawImage(sprite,x,y,fieldSize,fieldSize);
        return true;
    }

    private static Theme.FeldType getFeldType(Feld feld, Map<FieldDirection,Feld> neighbours){
        if(feld.getToken().isMovable()){
            // TODO: für die animation richtiges token bekommen ..
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
        if (type == FeldType.FOUREDGE || type == FeldType.TWOEDGE || type == FeldType.ZEROEDGE || type == FeldType.IDLE)
            return Theme.Position.DEFAULT;
        switch (type) {
            case ONEEDGE:
                if (neighbours.containsKey(FieldDirection.LEFT)) return Theme.Position.RIGHT;
                if (neighbours.containsKey(FieldDirection.RIGHT)) return Theme.Position.LEFT;
                if (neighbours.containsKey(FieldDirection.BOTTOM)) return Theme.Position.TOP;
                if (neighbours.containsKey(FieldDirection.TOP)) return Theme.Position.BOTTOM;
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
        }
        return null;

    }

    public Stage getStage(){
        return this.stage;
    }

    public double getWindowWidth(){
        return windowWidth;
    }
    public double getwindowHeight(){
        return windowHeight;}

  public double getMenuStartWidth(){
        double menuStartwidth;
      menuStartwidth=(windowWidth/2)-menuWidth/2;
        return menuStartwidth;
  }

  public double getMenuStartheight(){
      double menuStartheight;
      menuStartheight= windowHeight/2-menuHeight/2;
      return menuStartheight;
  }

}
