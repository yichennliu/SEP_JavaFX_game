package main;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Feld;
import view.View;

public class Controller {
    private View view;
    private Feld feld;

    public Controller(View view, Feld feld){
        this.view=view;
        this.feld=feld;
    }


}
