package controller;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.enums.Token;

import javafx.scene.input.MouseEvent;
import view.Theme;
import model.themeEditor.ThemeEditor;
import view.ThemeEditorView;

import java.io.File;
import java.io.FileInputStream;
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

        initAddButton();
        initRemoveButtons();
        initListViews();
        initTypeTabs();
    }

    private void updateListViewAndPreview(){
        ListView listView = themeEditorView.getListView();
        Label label = this.themeEditorView.getLabel();
        Token t = this.themeEditorView.getActiveToken();
        Theme.FeldType f = this.themeEditorView.getActiveFeldType();
        listView.setItems(this.themeEditor.getPathMap().get(t,f));
        setPreviewImage(t,f);
        label.setText("No image avaible for: " + t.name() + " - " + f.name());
    }

    private void initTypeTabs(){

        Map<Token,TabPane>  typeTabs = this.themeEditorView.getTypeTabs();
        TabPane tokenPane = this.themeEditorView.getTabs();
        tokenPane.getSelectionModel().selectedIndexProperty().addListener((a,b,c) -> {
            updateListViewAndPreview();
        });
        for(Token t : Token.values()){

                TabPane typePane = typeTabs.get(t);
                typePane.getSelectionModel().selectedIndexProperty().addListener((a,b,c) -> {
                   updateListViewAndPreview();
                });

        }
    }

    private void initAddButton() {
        Button addButton = themeEditorView.getAddButton();
        addButton.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            Token t = this.themeEditorView.getActiveToken();
            Theme.FeldType f = this.themeEditorView.getActiveFeldType();
            List<String> entries = themeEditor.getPathMap().get(t, f);
            List<Image> imageList = themeEditor.getImageMap().get(t, f);
                if(this.loadImage(t,f)){
                    ImageView imgV = themeEditorView.getPreview();
                    imgV.setImage(imageList.get(imageList.size()-1));
                }
            });
    }

    private void initRemoveButtons(){
        Button removeButton = themeEditorView.getRemoveButton();
            removeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                ListView listView = themeEditorView.getListView();
                    int index = listView.getSelectionModel().getSelectedIndex();
                    Token t = this.themeEditorView.getActiveToken();
                    Theme.FeldType f = this.themeEditorView.getActiveFeldType();
                    if(index!=-1){
                        List<String> entries = themeEditor.getPathMap().get(t, f);
                        List<Image> imageList = themeEditor.getImageMap().get(t, f);
                        entries.remove(index);
                        imageList.remove(index);
                        if(entries.size()<1){
                            removeButton.setDisable(true);
                            themeEditorView.getPreview().setImage(null);
                        }
                        else {
                            themeEditorView.getPreview().setImage(
                                    imageList.get(listView.getSelectionModel().getSelectedIndex()));
                        }
                    }
            });
    }

    private void initListViews(){
        ListView listView = themeEditorView.getListView();
        updateListViewAndPreview();
        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            int index = listView.getSelectionModel().getSelectedIndex();
            Token t = this.themeEditorView.getActiveToken();
            Theme.FeldType f = this.themeEditorView.getActiveFeldType();
            setPreviewImage(t,f);
        });

    }

    private void setPreviewImage(Token t, Theme.FeldType f){
        int index = themeEditorView.getListView().getSelectionModel().getSelectedIndex();
        ImageView preview = themeEditorView.getPreview();
        if(index!=-1){
            Button removeButton = themeEditorView.getRemoveButton();
            List<Image> imageList = themeEditor.getImageMap().get(t,f);
            preview.setImage(imageList.get(index));
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
            this.themeEditor.getImageMap().get(t, f).add(image);
            this.themeEditor.getPathMap().get(t, f).add(selectedFile.getPath());
            return true;
        }
        return false;
    } // controller
}

