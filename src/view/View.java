package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
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

    private void showGame(){
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


    public static void drawMap(GraphicsContext gc, Feld[][] feld, double fieldSize){
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
                gc.strokeRect(xPos, yPos, fieldSize, fieldSize);
                String text = feld[rowNum][colNum].toString();
                // Path verstecken, sonst ersten Buchstaben anzeigen
                gc.fillText(text.equals("PATH") ? "" : text.charAt(0)+"",xPos+fieldSize/2-15,yPos+fieldSize/2+5);
            }
        }
    }

    public Stage getStage(){
        return this.stage;
    }

}
