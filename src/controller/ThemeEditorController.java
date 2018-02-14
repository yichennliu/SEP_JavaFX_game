package controller;

import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.enums.Token;

import model.themeEditor.ThemeIO;
import model.themeEditor.SpriteSheet;
import view.themeEditor.Cell;
import model.themeEditor.Theme;
import view.themeEditor.ThemeEditorView;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;


public class ThemeEditorController {

    private ThemeEditorView themeEditorView;
    private Controller menuController;
    private Theme theme;
    private File initialDirImage = new File("src/");
    private boolean changes;

    public ThemeEditorController(ThemeEditorView view, Controller menuController) {
        this.themeEditorView = view;
        this.theme = new Theme("testTheme");
        this.menuController = menuController;
        preparePreviewGridPane();
        preparePreview();
        prepareTreeView();
        initFrameButtons();
        initFrameCountAndSizeFields();
        initNameInput();
        prepareIOButtons();
        prepareExitButton();
    }

    private void preparePreviewGridPane(){
       ToggleGroup positionButtonGroup = this.themeEditorView.getPositionButtonGroup();
       ImageView preview = this.themeEditorView.getPreview();
       GridPane previewGridPane = this.themeEditorView.getPreviewGridPane();

        positionButtonGroup.selectedToggleProperty().addListener(((a,b,c) -> {
            if (c!=null){
                Cell cell = this.themeEditorView.getSelectedItemProperty().get().getValue();
                Theme.Position p = (Theme.Position) this.themeEditorView.getPositionButtonGroup().getSelectedToggle().getUserData();

                preview.setCursor(Cursor.HAND);
                preview.setDisable(false);
                previewGridPane.setDisable(false);
                reloadTextFields();
                setPreviewImage(cell.getToken(),cell.getFeldType(),p,0);
            }
            else {
                preview.setCursor(Cursor.DEFAULT);
                previewGridPane.setDisable(true);
                preview.setImage(new Image("model/themeEditor/thumbnails/defaultPreview.png"));
            }
        }));
    }

    private void reloadTextFields(){
        Token t = getActiveToken();
        Theme.Position p = getActivePosition();
        Theme.FeldType f = getActiveFeldType();

        if (t!=null && p != null && f != null){
            SpriteSheet sheet = this.theme.getSpriteSheet(t,f,p);
            if(sheet!=null){
                this.themeEditorView.getSpriteSize().setText(Integer.toString(sheet.getSpriteSize()));
                this.themeEditorView.getFrameCount().setText(Integer.toString(sheet.getCount()));
                this.themeEditorView.getFramesPerSecondField().setText(Integer.toString(sheet.getFpt()));
            }
        }
    }

    private void preparePreview(){
        ImageView preview = themeEditorView.getPreview();
        preview.setOnMouseClicked( e-> {
            Cell cell = this.themeEditorView.getSelectedItemProperty().get().getValue();
            Theme.Position p = (Theme.Position) this.themeEditorView.getPositionButtonGroup().getSelectedToggle().getUserData();
            Image img = loadImage();
            SpriteSheet sheet;
            int spriteSize = BigInteger.valueOf((int) img.getWidth()).gcd(BigInteger.valueOf((int) img.getHeight())).intValue();
            int count = (int) (img.getWidth() *img.getHeight()) / (int) Math.pow(spriteSize,2);
            try {
                sheet = new SpriteSheet(img,spriteSize,count);
                theme.putSpriteSheet(cell.getToken(),cell.getFeldType(),p,sheet);
                setPreviewImage(cell.getToken(),cell.getFeldType(),p,0);
                changes = true;
            }
            catch(IllegalArgumentException exception){
                System.out.println("Fehler beim Erstellen des SpriteSheets");
            }

        });

        themeEditorView.getPreview().imageProperty().addListener((a,b,c) -> {
            if(c!=null){
                reloadTextFields();
            }
        });
    }

    private void prepareTreeView(){
        this.themeEditorView.getSelectedItemProperty().addListener((a,b,c) -> {
            if(c!= null && c!=b){
               Cell cell = c.getValue();

                if(cell.hasFeldType()){
                    reloadPositionPaneRoot(cell.getToken(),cell.getFeldType());
                }
                else {
                    ToggleGroup positionButtonGroup = this.themeEditorView.getPositionButtonGroup();
                    VBox positionButtons = this.themeEditorView.getPositionButtons();
                    positionButtonGroup.getToggles().clear();
                    positionButtons.getChildren().clear();
                }
            }

        });
    }

    private void initFrameButtons(){
        Button nextFButton = this.themeEditorView.getNextFrame();
        Button previousFButton = this.themeEditorView.getPreviousFrame();

        nextFButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            incrementFrameNumberField(true);
        });

        previousFButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> {
                incrementFrameNumberField(false);
        });

    }

    private void prepareExitButton(){
        this.themeEditorView.getExitButton().setOnAction(e -> {
            exit();
        });
    }

    private void exit(){
        this.theme = null;
        this.themeEditorView= null;
        this.menuController.startMenu();
    }

    /*reloads ToggleGroup of buttons and selects first option */
    private void reloadPositionPaneRoot(Token t, Theme.FeldType f){
        ToggleGroup positionButtonGroup = this.themeEditorView.getPositionButtonGroup();
        VBox positionButtons = this.themeEditorView.getPositionButtons();

        positionButtonGroup.getToggles().clear();
        positionButtons.getChildren().clear();
        this.themeEditorView.getFrameNumberField().setText(Integer.toString(0));

        for(Theme.Position p: f.getAllowedPositions()){
            ToggleButton toggleButton = new ToggleButton(p.name());
            if (!f.isMovable())
                toggleButton.setGraphic(new ImageView("model/themeEditor/thumbnails/position_thumbnails/"+f.name()+"/"+p.name()+".png"));
                toggleButton.setPrefHeight(8);
                toggleButton.setUserData(p);

                positionButtons.getChildren().add(toggleButton);
                positionButtonGroup.getToggles().add(toggleButton);
        }
        ObservableList<Toggle> toggles = positionButtonGroup.getToggles();
        if(toggles.size()>0) {
            positionButtonGroup.selectToggle(toggles.get(0));
        }
    }

    private void initFrameCountAndSizeFields(){
        TextField counter = this.themeEditorView.getFrameCount();
        TextField sizeField = this.themeEditorView.getSpriteSize();
        TextField fptField = this.themeEditorView.getFramesPerSecondField();

        counter.textProperty().addListener((a,b,c) -> {
            Token t = getActiveToken();
            Theme.FeldType f = getActiveFeldType();
            Theme.Position p = getActivePosition();
            if(t!=null && f!=null && p!=null){
                try {
                    int count = Integer.parseInt(c);
                    this.theme.getSpriteSheet(t,f,p).setCount(count);
                    setPreviewImage(t,f,p,0);
                    this.themeEditorView.getFrameNumberField().setText(Integer.toString(0));
                    counter.setStyle("-fx-text-fill: black");
                }
                catch(Exception e){
                    counter.setStyle("-fx-text-fill: red");
                }
            }
        });

        sizeField.textProperty().addListener((a,b,c) -> {
            Token t = getActiveToken();
            Theme.FeldType f = getActiveFeldType();
            Theme.Position p = getActivePosition();
            if(t!=null && f!=null && p!=null){
                try {
                    int size = Integer.parseInt(c);
                    this.theme.getSpriteSheet(t,f,p).setSpriteSize(size);
                    setPreviewImage(t,f,p,0);
                    this.themeEditorView.getFrameNumberField().setText(Integer.toString(0));
                    sizeField.setStyle("-fx-text-fill: black");
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                    sizeField.setStyle("-fx-text-fill: red");
                }
            }
        });

        fptField.textProperty().addListener((a,b,c) -> {
           Token t = getActiveToken();
           Theme.FeldType f = getActiveFeldType();
           Theme.Position p = getActivePosition();
           if(t!=null && f!=null && p!=null) {
               try {
                   int fpt = Integer.parseInt(c);
                   if (fpt>0){
                       this.theme.getSpriteSheet(t,f,p).setFpt(fpt);
                       fptField.setStyle("-fx-text-fill: black");
                       return;
                   }
               }
               catch(Exception e){}
               fptField.setStyle("-fx-text-fill: red");
           }
        });
    }

    private void incrementFrameNumberField(boolean increment) {
        TextField frameNumberField = this.themeEditorView.getFrameNumberField();
        int frame;
        int addition = (increment) ? 1 : -1;
        Token t = getActiveToken();
        Theme.FeldType f = getActiveFeldType();
        Theme.Position p = getActivePosition();
        SpriteSheet s = this.theme.getSpriteSheet(t,f,p);
        if(p==null || f == null || p == null || s == null) return;
        System.out.println("zurück!");
        int count = this.theme.getSpriteSheet(t,f,p).getCount();
        try {
            frame=Integer.parseInt(frameNumberField.getText());
            int newFrameCount = frame+addition;
            if(newFrameCount<count && newFrameCount>=0) {
                Image newFrame = this.theme.getSpriteSheet(t,f,p).getSprite(newFrameCount);
                this.themeEditorView.getPreview().setImage(newFrame);
                frameNumberField.setText(Integer.toString(newFrameCount));
            }
        }
        catch(NumberFormatException exception){
            System.out.println("Keine Zahl als Frame!!!");
        }
    }

    private void setPreviewImage(Token t, Theme.FeldType f, Theme.Position p, int frame){
        Image img;
        SpriteSheet sheet = theme.getSpriteSheet(t,f,p);
        ImageView preview = this.themeEditorView.getPreview();
        if(sheet!=null){
            img = sheet.getSprite(frame);
            if(img!=null){
                preview.setImage(img);
                return;
            }
        }
        preview.setImage(new Image("model/themeEditor/thumbnails/defaultPreview.png"));
    }

    private Token getActiveToken(){
        Cell cell = this.themeEditorView.getSelectedItemProperty().get().getValue();
        return cell.getToken();
    }

    private Theme.FeldType getActiveFeldType(){
        Cell cell = this.themeEditorView.getSelectedItemProperty().get().getValue();
        return cell.getFeldType();
    }

    private Theme.Position getActivePosition() {
        try {
            return (Theme.Position) this.themeEditorView.getPositionButtonGroup().getSelectedToggle().getUserData();
        }
        catch(Exception e){
            return null;
        }
    }

    private Image loadImage() {
        Stage imageOpenStage = new Stage();
        imageOpenStage.centerOnScreen();
        Image image;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initialDirImage);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(imageOpenStage);
        if (selectedFile != null) {
                initialDirImage = selectedFile.getParentFile();
            try {
                image = new Image(new FileInputStream(selectedFile.getPath()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        return image;
        }
        return null;
    } // controller

    private void prepareIOButtons(){
        this.themeEditorView.getExportButton().setOnAction( e -> {
            manageFileExport();
            themeEditorView.updateThemeList();
        });

        this.themeEditorView.getThemeChoiceBox().getSelectionModel().selectedItemProperty().addListener((a,b,c) -> {
            if(b!=null && b.equals(c) || c == null) return;
            manageThemeImport("src/json/theme/"+c);
        });
    }

    private void manageThemeImport(String path){
            Theme theme;
            try{
                theme = ThemeIO.importTheme(path);
                this.theme = theme;
                changes = false;
                themeEditorView.getNameInput().setText(theme.getName());
            }
            catch(Exception importException){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler beim Import");
                alert.setHeaderText("Datei fehlerhaft: ");
                alert.setContentText(importException.getMessage());
                alert.showAndWait();
            }
    }

    private void initNameInput(){
        this.themeEditorView.getNameInput().textProperty().addListener((a,b,c) -> {
            if(!this.theme.getName().equals(c)){
                this.theme.setName(c);
            }
        });
    }

    /*exports file only if theme has a name*/
    private void manageFileExport(){
        String name = this.theme.getName();
        if(name.equals("")) return;
        File file = new File("src/json/theme/"+name+".zip");
        System.out.println(theme.getName());
        if(file.exists()){
            if(!overwrite(name)){
                return;
            }
        }
        ThemeIO.exportTheme(this.theme);
        }



    /*shows alert-window, returns true if user clicked yes (overwrite)*/
    private boolean overwrite(String fileToOverwrite){
        ButtonType yes = new ButtonType("Überschreiben" , ButtonBar.ButtonData.YES);
        ButtonType close  = new ButtonType("Abbrechen" , ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.WARNING,"Soll die bestehende Datei " +fileToOverwrite+".zip überschrieben werden?",yes, close);
        alert.setTitle("Überschreiben?");
        alert.setHeaderText("Datei existiert bereits");
        return alert.showAndWait().get().equals(yes);
    }

}





