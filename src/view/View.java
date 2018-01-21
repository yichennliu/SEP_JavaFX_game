package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import model.enums.Neighbour;
import model.enums.Token;
import model.game.Feld;
import model.game.Level;
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
        stage.setScene(themeEditorView.getScene());
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
                    gc.fillText(text.equals("PATH") ? "" : text.charAt(0)+"",xPos+fieldSize/2-15,yPos+fieldSize/2+5);
                }
                else drawFeld(xPos, yPos, gc, feld[rowNum][colNum], fieldSize, theme);

            }
        }
    }

    private static void drawFeld(double x, double y, GraphicsContext gc, Feld feld, double fieldSize, Theme theme){
        Token t = feld.getToken();
//        FeldType = getFeldType();


    }

  /*  private static FeldType getFeldType(Feld feld){
        Map<Neighbour,Feld> neighbours = new HashMap<Neighbour,Feld>();
        for(Neighbour n: Neighbour.values() ){
            neighbours.put(n,feld.getNeighbour(n));
        }
        if(neighbours.get(Neighbour.LEFT).getToken() == feld.getToken()){

        }

    }*/


    public Stage getStage(){
        return this.stage;
    }

}
