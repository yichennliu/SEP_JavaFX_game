import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class View {

    Canvas canvas;
    GraphicsContext gc;
    Group root;
    Scene scene;

    public View(Level level, Stage stage){
        root = new Group();
        scene = new Scene(this.root);
        canvas = new Canvas(800,600);
        gc = canvas.getGraphicsContext2D();

        root.getChildren().addAll(canvas);

        stage.setScene(this.scene);
        stage.setTitle("BoulderDash");
        stage.show();
    }

    public void drawMap(Feld[][] map){
        if (map == null || map.length == 0) return;

        int mapWidth = map.length;
        int mapHeight = map[0].length;
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        double fieldLength = canvasWidth / mapWidth;
        double fieldHeight = canvasHeight / mapHeight;

        for(int y = 0; y < mapHeight; y++){
            for (int x = 0; x < mapWidth; x++){
                double xPos = x*fieldLength;
                double yPos = y*fieldHeight;
                this.gc.strokeRect(xPos, yPos, fieldLength, fieldHeight);
                this.gc.fillText(map[x][y].getToken().toString(),xPos+fieldLength/2,yPos+fieldHeight/2);
            }
        }
    }
}
