package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import model.ai.Robot;
import model.enums.Property;
import model.enums.WinningStatus;
import model.game.Level;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

public class GamePreview extends HBox{

    private Canvas staticCanvas;
    private Canvas animatedCanvas;
    private double fieldSize;
    private Robot robot;
    private Board board;
    private Timeline timeline;
    private Theme theme;
    private double speed =0.2;

    public GamePreview(double width, double height, double fieldSize){
        super();
        try {
            this.fieldSize = fieldSize;
            this.theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
            staticCanvas = new Canvas(width,height);
            animatedCanvas = new Canvas(width,height);
            Group canvasgroup  = new Group(staticCanvas,animatedCanvas);
            super.getChildren().addAll(canvasgroup,new Label("HAAAALLO"));
            staticCanvas.setCursor(Cursor.CLOSED_HAND);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void playLevel(Level level){
        Level levelCopy = level.clone();
        this.robot = new Robot(levelCopy,5);
        this.board = new Board(staticCanvas,animatedCanvas,levelCopy.getMap(),theme,fieldSize);

        EventHandler<ActionEvent> loop = e -> {
            levelCopy.setInputDirection(robot.getNextMove());

            /* Compute a tick */
            levelCopy.resetProperties();
            levelCopy.execPreRules();
            levelCopy.executeMainRules();
            levelCopy.execPostRules();

            levelCopy.setInputDirection(null);
            View.drawBoard(board,levelCopy.getMap(),theme,true);
            levelCopy.tick();

            if (levelCopy.getWinningStatus()==WinningStatus.LOST) {
                this.timeline.stop();
                this.timeline = null;
                playLevel(level);
            }
        };

        KeyFrame frame = new KeyFrame(Duration.seconds(speed), loop);
        this.timeline = new Timeline(frame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }

    public void pauseGame(){
        if(timeline!=null) timeline.stop();
    }

    public void resumeGame(){
        if(timeline!=null) timeline.play();
    }

    @Override
    public ObservableList<Node> getChildren(){
        return super.getChildren();
    }

}
