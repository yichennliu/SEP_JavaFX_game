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
import model.game.Level;

public class EndGameAlert extends Alert {

    private ButtonType retryButton;
    private ButtonType cancelExitButton;
    private ButtonType nextLevelButton;
    private Level level;
    private ImageView endGameImage = new ImageView();

    public EndGameAlert (){
        super(AlertType.INFORMATION);
        this.level = level;
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

        StackPane stackPane = new StackPane(this.endGameImage);

        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialogPane.setGraphic(stackPane);

        retryButton = new ButtonType("Restart level", ButtonBar.ButtonData.OTHER);
        cancelExitButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        nextLevelButton = new ButtonType("Next Level", ButtonBar.ButtonData.OTHER);

        this.getButtonTypes().setAll(

                retryButton,
                cancelExitButton,
                nextLevelButton

        );
    }

    public void setEndGameImage(Image endGameImage){

        this.endGameImage.setImage(endGameImage);
    }



    public ButtonType getRetryButton() {
        return retryButton;
    }

    public ButtonType getCancelExitButton() {
        return cancelExitButton;
    }

    public ButtonType getNextLevelButton() { return nextLevelButton; }

}
