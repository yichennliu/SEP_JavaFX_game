package model.misc;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import model.game.Level;
import model.themeEditor.Theme;
import view.View;

public class LevelSnapshot {

    public static Image snap(Theme theme, Level level){
        Group root = new Group();
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);
        Stage tempStage = new Stage();
        Scene tempScene = new Scene(root);

        int fieldSize = 10;
        int width = level.getWidth();
        int height = level.getHeight();

        canvas.setHeight(height*fieldSize);
        canvas.setWidth(width*fieldSize);

        View.drawMap(canvas.getGraphicsContext2D(),level.getMap(),fieldSize,theme);

       return canvas.snapshot(null,new WritableImage(width*fieldSize,height*fieldSize));
    }
}
