package model.misc;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import model.game.Level;
import model.themeEditor.Theme;
import view.Board;
import view.View;

public class LevelSnapshot {

    public static Image snap(Theme theme, Level level){
        Group root = new Group();
        Canvas canvas = new Canvas();
        Canvas canvas2 = new Canvas();
        int fieldSize = 10;

        int width = level.getWidth();
        int height = level.getHeight();

        canvas.setHeight(height*fieldSize);
        canvas.setWidth(width*fieldSize);
        canvas2.setHeight(canvas.getHeight());
        canvas2.setWidth(canvas.getWidth());

        Board board = new Board(canvas,canvas2, level.getMap(),theme,fieldSize);
        root.getChildren().addAll(canvas,canvas2);
        Stage tempStage = new Stage();
        Scene tempScene = new Scene(root);

        View.drawBoard(board,level.getMap(),theme,false);

       return canvas.snapshot(null,new WritableImage(width*fieldSize,height*fieldSize));
    }
}
