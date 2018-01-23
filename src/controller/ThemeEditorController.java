package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.enums.Token;

import javafx.scene.input.MouseEvent;
import model.themeEditor.SpriteSheet;
import model.themeEditor.ThemeIO;
import view.Theme;
import model.themeEditor.ThemeEditor;
import view.ThemeEditorView;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class ThemeEditorController {

    private ThemeEditorView themeEditorView;
    private ThemeEditor themeEditor;
    private Controller menuController;


    public ThemeEditorController(ThemeEditorView view, ThemeEditor themeEditor, Controller menuController) {
        this.themeEditorView = view;
        this.themeEditor = themeEditor;
        this.menuController = menuController;
        addThemeEditorViewComponents();
    }

    private void updateListViewAndPreview(){
        ListView listView = themeEditorView.getListView();
        Label label = this.themeEditorView.getLabel();
        Token t = this.themeEditorView.getActiveToken();
        Theme.FeldType f = this.themeEditorView.getActiveFeldType();
        listView.setItems(this.themeEditor.getSprites().get(t,f));
        setPreviewImage(t,f,listView.getSelectionModel().getSelectedIndex());
        label.setText("No image avaible for: " + t.name() + " - " + f.name());
    }

    private void initSaveButton(){
        Button saveButton = this.themeEditorView.getSaveButton();

        saveButton.setOnAction(e -> {
            System.out.println("saveTheme gestartet");
            ThemeIO.exportTheme(this.themeEditor.makeTheme());
        });
    }

    private void initTypeTabs(){

        Map<Token,TabPane>  typeTabs = this.themeEditorView.getTypeTabs();
        TabPane tokenPane = this.themeEditorView.getTabs();
        tokenPane.getSelectionModel().selectedIndexProperty().addListener((a,b,c) -> {
            updateListViewAndPreview();
        });
        for(Token t : Token.values()){

                TabPane typePane = typeTabs.get(t);
                typePane.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> {
                   updateListViewAndPreview();
                });

        }
    }

    private void initAddButton() {
        Button addButton = themeEditorView.getAddButton();
        addButton.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            Token t = this.themeEditorView.getActiveToken();
            Theme.FeldType f = this.themeEditorView.getActiveFeldType();
            List<SpriteSheet> imageList = themeEditor.getSprites().get(t, f);
                if(this.loadImage(t,f)){
                    ImageView imgV = themeEditorView.getPreview();
                    imgV.setImage(imageList.get(imageList.size()-1).getSprite(Theme.Position.DEFAULT,0));
                }
            });
    }

    private void initRemoveButton(){
        Button removeButton = themeEditorView.getRemoveButton();
            removeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                ListView listView = themeEditorView.getListView();
                    int index = listView.getSelectionModel().getSelectedIndex();
                    Token t = this.themeEditorView.getActiveToken();
                    Theme.FeldType f = this.themeEditorView.getActiveFeldType();
                    if(index!=-1){
                        List<SpriteSheet> sprites = themeEditor.getSprites().get(t, f);
                        sprites.remove(index);
                        if(sprites.size()<1){
                            removeButton.setDisable(true);
                            themeEditorView.getPreview().setImage(null);
                        }
                        else {
                            themeEditorView.getPreview().setImage(
                                    sprites.get(listView.getSelectionModel().getSelectedIndex()).getSprite(Theme.Position.DEFAULT,0));
                        }
                    }
            });
    }

    private void initSizeInput(){
        TextField input = this.themeEditorView.getSizeInput();
        input.setDisable(true);
        input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int spriteSize;
                try{
                    spriteSize = Integer.parseInt(newValue);
                    Token t = themeEditorView.getActiveToken();
                    Theme.FeldType f = themeEditorView.getActiveFeldType();
                    int index = themeEditorView.getListView().getSelectionModel().getSelectedIndex();
                    try {
                        themeEditor.getSprites().get(t,f).get(index).setSpriteSize(spriteSize);
                        setPreviewImage(t,f,index);
                        input.setStyle("-fx-text-fill:black");
                    }
                    catch(Exception e){
                        input.setStyle("-fx-text-fill:red");
                    }
                }
                catch(NumberFormatException e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private void initListViews(){
        ListView listView = themeEditorView.getListView();
        listView.getSelectionModel().selectedIndexProperty().addListener((a,b,c) ->{
                int index = c.intValue();
                Token t = themeEditorView.getActiveToken();
                Theme.FeldType f = themeEditorView.getActiveFeldType();
                setPreviewImage(t,f,index);
                updateSizeInput(t,f,index);
        });
//        listView.setCellFactory(createCellFactory());
        updateListViewAndPreview();

    }

    private void updateSizeInput(Token t, Theme.FeldType f, int index) {
        SpriteSheet s;
        TextField sizeInput = themeEditorView.getSizeInput();
        if(index!=-1){
            sizeInput.setDisable(false);
            try {
                s = this.themeEditor.getSprites().get(t, f).get(index);
                if (s != null) {
                    sizeInput.setText(Integer.toString(s.getSpriteSize()));
                }
            }
            catch(Exception e){
                sizeInput.setText(Integer.toString(-1));
            }
        }
        else {
            sizeInput.setText("");
            sizeInput.setDisable(true);
        }

    }

    private void setPreviewImage(Token t, Theme.FeldType f, int index){
        ImageView preview = themeEditorView.getPreview();
        if(index!=-1){
            Button removeButton = themeEditorView.getRemoveButton();
            ObservableList<SpriteSheet> imageList = themeEditor.getSprites().get(t,f);
            preview.setImage(new Image("view/loading.gif"));
            Image img =imageList.get(index).getSprite(Theme.Position.DEFAULT,0);
            preview.setImage(img);
            removeButton.setDisable(false);
        }
        else {
            preview.setImage(null);
        }
    }

    private boolean loadImage(Token t, Theme.FeldType f) {
        Stage imageOpenStage = new Stage();
        imageOpenStage.centerOnScreen();
        Image image;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(imageOpenStage);
        if (selectedFile != null) {

            try {
                image = new Image(new FileInputStream(selectedFile.getPath()));
             } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
            SpriteSheet newSpriteSheet;
            int spriteSize = BigInteger.valueOf((int) image.getWidth()).gcd(BigInteger.valueOf((int) image.getHeight())).intValue();
            System.out.println(spriteSize);
            newSpriteSheet = new SpriteSheet(image,f,spriteSize);
            newSpriteSheet.setPath(selectedFile.getPath());
            this.themeEditor.getSprites().get(t,f).add(newSpriteSheet);

            return true;
        }
        return false;
    } // controller

    private void back(){
        Button backButton = themeEditorView.getBackButton();
        backButton.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            this.menuController.startPrimaryPage();

        });
    }

    public void setThemeEditorView(ThemeEditorView themeEditorView) {
        this.themeEditorView = themeEditorView;
        addThemeEditorViewComponents();
    }

    private void addThemeEditorViewComponents() {
        initAddButton();
        initRemoveButton();
        initListViews();
        initTypeTabs();
        initSizeInput();
        initSaveButton();
        back();
    }
}

