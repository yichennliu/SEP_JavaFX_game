package controller;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
import view.Cell;
import model.themeEditor.Theme;
import view.ThemeEditorView;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;


public class ThemeEditorController {

    private ThemeEditorView themeEditorView;
    private Controller menuController;
    private Theme theme;
    private File initialDirImage = new File("src/");

    public ThemeEditorController(ThemeEditorView view, Controller menuController) {
        this.themeEditorView = view;
        this.theme = new Theme("testTheme");
        this.menuController = menuController;
        preparePreviewGridPane();
        preparePreview();
        prepareTreeView();
        initFrameButtons();
        initFrameCountAndSizeFields();
        prepareIOButtons();
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
        System.out.println("reload Textfield!");
        Token t = getActiveToken();
        Theme.Position p = getActivePosition();
        Theme.FeldType f = getActiveFeldType();

        if (t!=null && p != null && f != null){
            SpriteSheet sheet = this.theme.getSpriteSheet(t,f,p);
            if(sheet!=null){
                this.themeEditorView.getSpriteSize().setText(Integer.toString(sheet.getSpriteSize()));
                this.themeEditorView.getFrameCount().setText(Integer.toString(sheet.getCount()));
            }
        }
    }

    private void preparePreview(){
        themeEditorView.getPreview().addEventHandler(MouseEvent.MOUSE_CLICKED, e-> {
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
            }
            catch(IllegalArgumentException exception){
                System.out.println("Fehler beim Erstellen des SpriteSheets");
            }

        });

        themeEditorView.getPreview().imageProperty().addListener((a,b,c) -> {
            if(c!=null){
                System.out.println("bild geändert");
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
    }

    private void initFrameCountAndSizeFields(){
        TextField counter = this.themeEditorView.getFrameCount();
        TextField sizeField = this.themeEditorView.getSpriteSize();

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
                    System.out.println(e.getMessage());
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
            ThemeIO.exportTheme(this.theme);
        });

        this.themeEditorView.getImportButton().setOnAction(e -> {
            Theme theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
            this.theme = theme;
        });
    }
}

