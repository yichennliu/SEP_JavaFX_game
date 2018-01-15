package main;

import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Feld;
import model.Level;
import view.View;

public class Controller {
    private View view;
    private Feld feld;
    private Level level;
    private View.Mode currentMode;

    public Controller(View view, Feld feld, Level level){
        this.view=view;
        this.feld=feld;
        this.level=level;
        this.currentMode = View.Mode.GAME;
    }

    EventHandler<ActionEvent> loop = e -> {
        this.level.tick();
        this.view.update(currentMode);
    };

    KeyFrame frame = new KeyFrame(Duration.seconds(1/5),loop);



}
