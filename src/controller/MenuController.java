package controller;

import com.apple.laf.AquaButtonBorder.Toggle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.MenuView;

import static com.sun.javafx.tools.resource.DeployResource.Type.icon;


public class MenuController {

    private MenuView menuView;
    private Object model; // Todo: Menu-Model erstellen
    private Controller menuController;
    private ToggleGroup group;


    public MenuController(MenuView menuView, Object model, Controller menuController){
        this.menuView = menuView;
        this.model = model;
        this.menuController = menuController;

        menuView.getGameButton().setOnAction(e -> {
            this.menuController.startGame();
        });

        menuView.getThemeEditorButton().setOnAction(e -> {
            this.menuController.startThemeEditor();
        });



    }
}
