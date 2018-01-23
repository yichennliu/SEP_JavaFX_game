package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import model.enums.Token;
import model.game.Feld;
import model.game.Level;

public class View {

    private GraphicsContext gameGC;
    private GraphicsContext  editorGC;

    private Stage stage;
    private Object currentScene;

    private Level level;
    private int maxSize = 1000;

    private double windowWidth = 800;
    private double windowHeight = 600;
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
        gc.setTransform(actualTransformation);
        gc.setFont(new Font(2));

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        int mapWidth = feld[0].length;
        int mapHeight = feld.length;

        for(int rowNum = 0; rowNum < mapHeight; rowNum++){
            for (int colNum = 0; colNum < mapWidth; colNum++){
                double xPos = colNum* fieldSize;
                double yPos = rowNum*fieldSize;
                if(theme == null) {
                    gc.strokeRect(xPos, yPos, fieldSize, fieldSize);
                    String text = feld[rowNum][colNum].toString();
                    // Path verstecken, sonst ersten Buchstaben anzeigen
                    gc.fillText(text.equals("PATH") ? "" : (text.equals("ME") ? "ME" : text.charAt(0)+""),
                            xPos+fieldSize/2-7,yPos+fieldSize/2+5);
                }
                else drawFeld(xPos, yPos, gc, feld[rowNum][colNum], fieldSize, theme);

            }
        }
    }

    private static void drawFeld(double x, double y, GraphicsContext gc, Feld feld, double fieldSize, Theme theme){
        Map<Neighbour,Feld> neighbours = getEdgeNeighbours(feld,true);
        Token t = feld.getToken();
        FeldType f = getFeldType(feld,neighbours);
        Theme.Position p = getFeldPosition(feld,f,neighbours);
        gc.strokeRect(x,y,fieldSize,fieldSize);
        gc.fillText("Token "+t.name(),x+1,y+4);
        gc.fillText("Type " +f.name() ,x+1,y+6);
        gc.fillText( "Pos " + p.name(),x+1,y+8);
        gc.fillText("count " + neighbours.size(),x+1,y+10);
        }

    private static FeldType getFeldType(Feld feld,Map<Neighbour,Feld> neighbours){
        int neighbourCount = neighbours.size();
        switch(neighbourCount){
            case 0: return FeldType.ZEROEDGE;
            case 1: return FeldType.ONEEDGE;
            case 2:
                if(neighbours.get(Neighbour.LEFT)!=null && neighbours.get(Neighbour.RIGHT)!=null ||
                        neighbours.get(Neighbour.TOP)!=null && neighbours.get(Neighbour.BOTTOM)!=null){
                    return FeldType.TWOEDGE;
                }
                else return FeldType.TWOEDGE_CORNER;
            case 3: return FeldType.THREEEDGE;
            default: return FeldType.FOUREDGE;
        }
    }

    private static Map<Neighbour,Feld> getEdgeNeighbours(Feld feld, boolean sameToken) {
        Map<Neighbour, Feld> neighbours = new HashMap<Neighbour, Feld>();
        Token token = feld.getToken();
        for (Neighbour n : Neighbour.values()) { // nur die direkten Nachbarn (ohne schräg oben/unten, etc.)
            if (n != Neighbour.LEFTBOTTOM && n != Neighbour.LEFTTOP && n != Neighbour.RIGHTBOTTOM && n != Neighbour.RIGHTTOP) {
                Feld neighbour = feld.getNeighbour(n);
                if (sameToken && (neighbour != null && neighbour.getToken() != token)) continue;
                if(neighbour!=null) neighbours.put(n, neighbour);
            }
        }
        return neighbours;
    }

    private static Theme.Position getFeldPosition (Feld feld, FeldType type, Map<Neighbour,Feld> neighbours) {
        if (type == FeldType.FOUREDGE || type == FeldType.TWOEDGE || type == FeldType.ZEROEDGE)
            return Theme.Position.DEFAULT;
        switch (type) {
            case ONEEDGE:
                if (neighbours.containsKey(Neighbour.LEFT)) return Theme.Position.RIGHT;
                if (neighbours.containsKey(Neighbour.RIGHT)) return Theme.Position.LEFT;
                if (neighbours.containsKey(Neighbour.BOTTOM)) return Theme.Position.TOP;
                if (neighbours.containsKey(Neighbour.TOP)) return Theme.Position.BOTTOM;
            case TWOEDGE_CORNER:
                if (neighbours.containsKey(Neighbour.TOP) && neighbours.containsKey(Neighbour.RIGHT))
                    return Theme.Position.BOTTOMLEFT;
                if (neighbours.containsKey(Neighbour.TOP) && neighbours.containsKey(Neighbour.LEFT))
                    return Theme.Position.BOTTOMRIGHT;
                if (neighbours.containsKey(Neighbour.BOTTOM) && neighbours.containsKey(Neighbour.LEFT))
                    return Theme.Position.TOPRIGHT;
                if (neighbours.containsKey(Neighbour.BOTTOM) && neighbours.containsKey(Neighbour.RIGHT))
                    return Theme.Position.TOPLEFT;
            case THREEEDGE:
                if (!neighbours.containsKey(Neighbour.TOP)) return Theme.Position.TOP;
                if (!neighbours.containsKey(Neighbour.BOTTOM)) return Theme.Position.BOTTOM;
                if (!neighbours.containsKey(Neighbour.RIGHT)) return Theme.Position.RIGHT;
                if (!neighbours.containsKey(Neighbour.LEFT)) return Theme.Position.LEFT;
        }
        return null;

    }

    public Stage getStage(){
        return this.stage;
    }

}
