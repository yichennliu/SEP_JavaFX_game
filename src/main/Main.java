package main;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Level;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Level level = LevelImporter.createLevel("json/text.json") ;
        View view = new View(level,primaryStage);
        view.drawMap();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
