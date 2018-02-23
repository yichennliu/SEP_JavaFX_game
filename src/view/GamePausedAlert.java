package view;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GamePausedAlert extends Alert {

    private ButtonType saveButton;
    private ButtonType saveExitButton;
    private ButtonType exitButton;
    private ButtonType retryButton;
    private ButtonType cancelButton;

    public GamePausedAlert() {
        super(Alert.AlertType.CONFIRMATION);

        this.setTitle("Exit or Save");
        this.setHeaderText("Do you want to save or exit the game?");
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setStyle("-fx-background-color: black;");
        dialogPane.getStyleClass().remove("alert");

        GridPane grid = (GridPane) dialogPane.lookup(".header-panel");
        grid.setStyle("-fx-background-color: black; "
                + "-fx-font: bold normal 20pt \"Arial\";"+"-fx-text-fill: red;");

        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 30px; "
                + "-fx-font-weight: bold;" + "-fx-fill: white;");

        ButtonBar buttonBar = (ButtonBar) this.getDialogPane().lookup(".button-bar");
        buttonBar.setStyle("-fx-background-color:black;"+
                "-fx-text-fill:white;"+ "-fx-wrap-text: true;"+
                "-fx-effect: dropshadow(three-pass-box, yellow, 10.0, 0.0, 0.0, 0.0);"+
                "-fx-cursor:hand;");

        StackPane stackPane = new StackPane(new ImageView(
                new Image(getClass().getResourceAsStream("images/Exit.png"))));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);

        saveButton = new ButtonType("Save", ButtonBar.ButtonData.OTHER);
        saveExitButton = new ButtonType("Save & Exit", ButtonBar.ButtonData.OTHER);
        exitButton = new ButtonType("Exit", ButtonBar.ButtonData.OTHER);
        retryButton = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
        cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getButtonTypes().setAll(
                saveButton,
                saveExitButton,
                exitButton,
                retryButton,
                cancelButton
        );
    }

    public ButtonType getSaveButton() {
        return saveButton;
    }

    public ButtonType getSaveExitButton() {
        return saveExitButton;
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
