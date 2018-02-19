package view;

/**
 * Created by aidabakhtiari on 09.02.18.
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.LevelFactory;
import model.enums.Medal;
import model.game.Level;
import model.game.MedalStatus;
import model.misc.LevelSnapshot;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public  class ContentFrame extends StackPane {

    private Button gameButton;
    private Button continueButton;
    private Button saveButton;
    private Button restartLevel;
    private Button levelButtons;
    private Button themeEditorButton;
    private Button helpbutton;  private Button close;
    private int currentItem = 0;
    private boolean  showMenuItems= false;
    private VBox menuBox,helpVbox;
    private ArrayList listlevelButtons;
    private Scene scene;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane,helpVboxScrollPane;
    private ScrollPane helpScrollPane;
    private double widthLinks,heightLinks ;
    private int buttonfactor=4;


    public ContentFrame(double widthLinks, double heightLinks, MenuView menuView) {
        this.widthLinks=widthLinks;
        this.heightLinks=heightLinks;
        this.menuView = menuView;
        this.gameButton = createButton("S T A R T");
        this.levelButtons = createButton("L E V E L S   ");
        this.themeEditorButton = createButton("T H E M E ");
        this.helpbutton = createButton("H E L P");
        this.close = createButton("C L O S E ");
        this.continueButton = createButton("C O N T I N U E");
        this.saveButton = createButton("S A V E ");
        setAlignment(Pos.CENTER);

        VBox menuGamePause = new VBox(15,saveButton,continueButton);
        menuGamePause.setVisible(false);

        VBox menuGameMain = new VBox(15,gameButton,levelButtons,themeEditorButton,helpbutton,close);
        VBox menuVboxlinks = new VBox(menuGamePause,menuGameMain);
        menuVboxlinks.setMinSize(widthLinks,heightLinks*2);
        menuVboxlinks.setId("vboxLinks");

    Label boulderdash = new Label(" B o u l d e r d a s h ");
        HBox boulderdashHbox = new HBox(boulderdash);
       boulderdashHbox.setAlignment(Pos.BOTTOM_LEFT);
             boulderdash.setId("boulderdash");
          boulderdash.setTranslateX(widthLinks/4);
       // VBox ganz =  new VBox(boulderdashHbox,vboxLinks);



        this.scene= new Scene(menuVboxlinks);
        this.listlevelButtons = new ArrayList<Button>();
        menuBox = createLevelMenuItems();
        menuBox.getStyleClass().add("levelbox");
        levelItemScrollPane = createscrollPane(menuBox);


        levelButtons.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
             if(levelItemScrollPane.isVisible()){
                 levelItemScrollPane.setVisible(false);
             }
             else levelItemScrollPane.setVisible(true);
                helpVboxScrollPane.setVisible(false);
            }
        });


        /*todo    arraylist of buttons*/
        setHover(gameButton);
        setHover(levelButtons);
        setHover(themeEditorButton);
        setHover(helpbutton);
        setHover(close);
        setHover(saveButton);

        close.setOnAction(e -> Platform.exit());

        helpVbox=createHelpMenuItem();
        helpVboxScrollPane= createscrollPane(helpVbox);

        helpbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(helpVboxScrollPane.isVisible()){
                    helpVboxScrollPane.setVisible(false);

                }
                else{ helpVboxScrollPane.setVisible(true);
                levelItemScrollPane.setVisible(false);}
            }

        });

        getChildren().addAll(menuVboxlinks, levelItemScrollPane,helpVboxScrollPane);
    }





    public ScrollPane createscrollPane(VBox scrollVbox){

        ScrollPane ScrollPane = new ScrollPane(scrollVbox);
        ScrollPane.setId("scroll");
        ScrollPane.setTranslateX(widthLinks/2);
        //  levelItemScrollPane.setTranslateY(heightLinks/8);
        ScrollPane.setMinSize(widthLinks*2,heightLinks/2);
        ScrollPane.setVisible(false);


        return ScrollPane;
    }



    /////////////ende konstruktor
    private String[] scanLevelDirectory() {
        File dir = new File("src/json/level");
        return dir.list();
    }


    public Button createButton(String titel ) {
       Button button= new Button(titel);
        button.setMinWidth(widthLinks/buttonfactor);
        return button;
    }



    public LevelItem getMenuItem(int index) {
        return (LevelItem)menuBox.getChildren().get(index);
    }


    public ArrayList getListlevelButtons() {
        return listlevelButtons;
    }

    private class LevelItem extends HBox {
        private final Font FONT = Font.font("", FontWeight.BOLD, 18);
        private Button levelButton; private Text information;
        private Runnable script;


    public LevelItem(String menuItemName, String info, ImageView image, String path) {
        super(5);
        setAlignment(Pos.CENTER);
        levelButton = new Button(menuItemName);
        information = new Text(info);
        information.setEffect(new GaussianBlur(1));
        levelButton.setFont(FONT);
        levelButton.setEffect(new GaussianBlur(2));
        levelButton.setMinWidth(widthLinks/2);
        levelButton.setMaxWidth(widthLinks/2);

        HBox menu = new HBox(levelButton);
        menu.setMinSize(widthLinks/2,100);
        menu.setAlignment(Pos.CENTER);
        image.setFitWidth(55);
        image.setFitHeight(55);

        VBox vboxLevelInformation= new VBox(information);

         vboxLevelInformation.setMinSize(widthLinks/2,heightLinks/2);
        vboxLevelInformation.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll( vboxLevelInformation,menu,image);
       // setActive(false);
        // setOnActivate(() -> System.out.println(menuItemName + " aktiv geworden"));
        levelButton.setUserData(path);
        listlevelButtons.add(levelButton);


    }



}

    private VBox createLevelMenuItems(){

        menuBox = new VBox(5 );
        menuBox.setAlignment(Pos.TOP_CENTER);

        Theme theme = null;
        try {
            theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        }
        catch(Exception e) {
            System.out.println("Theme not found / corrupt file");
        }

        for (String path : scanLevelDirectory()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/level/"+path));
            Level level = LevelFactory.importLevel("src/json/level/"+path);
            ImageView snapshotview =  new ImageView(snapshot);
            String levelText = "Medaillen: "+this.getMedalsFormatted(level.getJsonPath());
            menuBox.getChildren().add( 0, new LevelItem( level.getName(),levelText,snapshotview,path));
            snapshotview.setFitWidth(100);
            snapshotview.setFitHeight(100);

         //   getMenuItem(0).setActive(true);

        }

        return menuBox;

    }//ende create levele

    /**
     * @param path Level path
     * @return Medals formatted as String
     */
    private String getMedalsFormatted(String path) {
        Map<String,MedalStatus> medalStatuses = this.menuView.getMedalStatuses();
        MedalStatus medalStatus = medalStatuses.get(path);

        String medals = "keine";
        List<String> medalsList = new ArrayList();
        if (medalStatus != null) {
            if (medalStatus.has(Medal.GOLD)) {
                medalsList.add(Medal.GOLD.getDisplayName());
            }
            if (medalStatus.has(Medal.SILVER)) {
                medalsList.add(Medal.SILVER.getDisplayName());
            }
            if (medalStatus.has(Medal.BRONZE)) {
                medalsList.add(Medal.BRONZE.getDisplayName());
            }

            if (!medalsList.isEmpty()) {
                medals = String.join(", ", medalsList);
            }
        }

        return medals;
    }


    private VBox createHelpMenuItem(){
        helpVbox = new VBox();
        Label shortcut = new Label(" Ctr "+" + "+" schift");
        Label description = new Label(" move forward");
        HBox hboxHelp = new HBox(shortcut,description);

        helpVbox.getChildren().addAll(hboxHelp);

        return helpVbox;
    }









    public void doHover(Button button){

        button.getStyleClass().add("hover");
        button.setCursor(Cursor.HAND);
      //  button.setText("hier kannst du "+text);
    }

    public void unHover(Button button){

        button.getStyleClass().clear();
        button.getStyleClass().add("button");


    }





    public void setHover(Button button){

        button.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                doHover(button);

            }
        });

        button.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                unHover(button);

            }
        });




    }





    public Button getContinueButton() {
        return continueButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getGameButton() {
        return gameButton;
    }

    public Button getHelpButton() {
        return levelButtons;
    }

    public Button getThemeEditorButton() {
        return themeEditorButton;
    }




} // ende class content frame

