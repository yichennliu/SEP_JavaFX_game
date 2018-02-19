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

public class EndGameAlert extends Alert {

    private ButtonType retryButton;
    private ButtonType cancelExitButton;

    public EndGameAlert (){
        super(AlertType.INFORMATION);

        this.setTitle("Exit or Save");
        this.setHeaderText("Do you want to save or exit the game?");
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setStyle("-fx-background-color: black;"+"-fx-text-fill: white;");
        dialogPane.getStyleClass().remove("alert");

        GridPane grid = (GridPane)dialogPane.lookup(".header-panel");
        grid.setStyle("-fx-background-color: black; "
                + "-fx-font: bold normal 20pt \"Arial\";"+"-fx-text-fill: white;");

        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 30px; "
                + "-fx-font-weight: bold;" + "-fx-text-fill: white;");

        ButtonBar buttonBar = (ButtonBar)this.getDialogPane().lookup(".button-bar");
        buttonBar.setStyle("-fx-background-color:black;"+
                "-fx-text-fill: white;"+ "-fx-wrap-text: true;"+
                "-fx-effect: dropshadow(three-pass-box, yellow, 10.0, 0.0, 0.0, 0.0);"+
                "-fx-cursor:hand;");

        StackPane stackPane = new StackPane(new ImageView(
                new Image(getClass().getResourceAsStream("images/fire.png"))));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);

        retryButton = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
        cancelExitButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

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

}
