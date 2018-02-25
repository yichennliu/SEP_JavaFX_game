package main;

import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;
import view.View;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        View view = new View(primaryStage);
        Controller controller = new Controller(view);
        controller.startMenu();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
