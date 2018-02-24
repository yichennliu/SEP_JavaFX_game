package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.game.Level;

import java.io.File;

public class EndGameAlert extends Alert {

    private ButtonType retryButton;
    private ButtonType cancelExitButton;
    private ButtonType nextLevelButton;
    private Level level;
    private String stylesheet;
    private MenuView menuView;

    public EndGameAlert (){
        super(AlertType.INFORMATION);
        this.stylesheet = menuView.fileToStylesheetString(new File("src/view/style.css"));
        this.setTitle("Exit or Save");
        this.setHeaderText("Do you want to save or exit the game?");
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getStylesheets().add(stylesheet);
        dialogPane.getStyleClass().add(".dialog-pane");

        StackPane stackPane = new StackPane(new ImageView(
                new Image(getClass().getResourceAsStream("images/EndGame.png"))));
        stackPane.setPrefSize(15, 15);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);

        retryButton = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
        cancelExitButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        nextLevelButton = new ButtonType("Next Level", ButtonBar.ButtonData.OTHER);

        this.getButtonTypes().setAll(

                retryButton,
                cancelExitButton
        );
    }


    public ButtonType getRetryButton() {
        return retryButton;
    }

    public ButtonType getCancelExitButton() {
        return cancelExitButton;
    }

    public ButtonType getNextLevelButton() { return nextLevelButton; }

}
