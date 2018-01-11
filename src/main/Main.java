package main;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Level;
import view.View;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Level level = LevelImporter.importLevel("json/text.json") ;
        View view = new View(level,primaryStage);
        view.update(View.Mode.GAME);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
