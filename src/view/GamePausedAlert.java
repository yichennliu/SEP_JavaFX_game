package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;

public class GamePausedAlert extends Alert {

    private ButtonType saveButton;
    private ButtonType exitButton;
    private ButtonType retryButton;
    private ButtonType cancelButton;
    private String stylesheet;
    private MenuView menuView;

    public GamePausedAlert() {
        super(Alert.AlertType.CONFIRMATION);
        this.stylesheet = menuView.fileToStylesheetString(new File("src/view/style.css"));
        this.setTitle("Exit or Save");
        this.setHeaderText("Do you want to save or exit the game?");

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getStylesheets().add(stylesheet);
        dialogPane.getStyleClass().add(".dialog-pane");

        StackPane stackPane = new StackPane(new ImageView(
                new Image(getClass().getResourceAsStream("images/Exit.png"))));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);

        saveButton = new ButtonType("Save", ButtonBar.ButtonData.OTHER);
        exitButton = new ButtonType("Exit", ButtonBar.ButtonData.OTHER);
        retryButton = new ButtonType("Restart", ButtonBar.ButtonData.OTHER);
        cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getButtonTypes().setAll(
                saveButton,
                exitButton,
                retryButton,
                cancelButton
        );
    }

    public ButtonType getSaveButton() {
        return saveButton;
    }

    public ButtonType getExitButton() {
        return exitButton;
    }

    public ButtonType getRetryButton() {
        return retryButton;
    }

    public ButtonType getCancelButton() {
        return cancelButton;
    }

}
