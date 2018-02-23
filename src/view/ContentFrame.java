package view;

/**
 * Created by aidabakhtiari on 09.02.18.
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.LevelFactory;
import model.enums.Medal;
import model.game.Level;
import model.game.MedalStatus;
import model.misc.LevelSnapshot;
import model.misc.UsefulMethods;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public  class ContentFrame extends StackPane {


    private Button themeEditorButton,loadtheme,levelButtons,saveButton,gameButton;
    private Button helpbutton;  private Button close;
    private Button levelEditorButton;
    private Boolean showSavebutton;
    private VBox levelVbox,helpVbox,savedGameVbox,menuVboxlinks;
    private ArrayList<LevelItem> listlevelButtons;
    private ArrayList<Button> listSavedGameButtons;
    private Scene scene;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane,helpVboxScrollPane;
    private double widthLinks,heightLinks ;
    private int buttonfactor=4;
    private Group root;
    private final Font FONT = Font.font("", FontWeight.BOLD, 18);
    private final Font header = Font.font("", FontWeight.BOLD, 28);


    public Button getLevelButtons() {
        return levelButtons;
    }

    public Button getClose() {
        return close;
    }

    public Boolean getShowSavebutton() {
        return showSavebutton;
    }


    public ContentFrame(double widthLinks,double heightLinks,MenuView menuView) {
        this.menuView = menuView;
        this.widthLinks=menuView.getWidth();
        this.heightLinks=menuView.getHeight();
        this.gameButton = createButton("S T A R T");
        this.levelButtons = createButton("L E V E L S");
        this.loadtheme = createButton(" L O A D - T H E M E");
        this.themeEditorButton = createButton(" T H E M E - E D I T O R");
        this.helpbutton = createButton("H E L P");
        this.close = createButton("C L O S E ");
        this.levelEditorButton = createButton("L E V E L - E D I T O R");
        this.saveButton = createButton("S A V E D  G A M E ");
         menuVboxlinks = new VBox(15, gameButton, levelButtons, saveButton, loadtheme, themeEditorButton, levelEditorButton, helpbutton, close);
         menuVboxlinks.setMinSize((widthLinks/2),heightLinks);
        menuVboxlinks.setMaxSize((widthLinks/2),heightLinks);
        menuVboxlinks.setAlignment(Pos.TOP_LEFT);
         menuVboxlinks.setId("vboxLinks");
        this.listSavedGameButtons = new ArrayList<Button>();
        this.listlevelButtons = new ArrayList<LevelItem>();
        this.levelVbox = createLevelMenuItems();
        this.levelVbox.getStyleClass().add("levelbox");
        this.levelItemScrollPane = createscrollPane(levelVbox);
        levelButtons.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
             if(levelItemScrollPane.isVisible()){
                 levelItemScrollPane.setVisible(false);
             }
             else levelItemScrollPane.setVisible(true);
                helpVboxScrollPane.setVisible(false);
            }
        });
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
        ScrollPane.setMinSize(widthLinks/2,heightLinks);
        ScrollPane.setMaxSize(widthLinks/2,heightLinks);
        ScrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        ScrollPane.setVisible(false);
        return ScrollPane;
    }


    public Button createButton(String titel ) {
       Button button= new Button(titel);
        button.setMinWidth(widthLinks/buttonfactor);
        return button;
    }

    public ArrayList<LevelItem> getListlevelButtons() {
        return listlevelButtons;
    }

    public ArrayList getListSavedGameButtons(){
        return listSavedGameButtons;
    }

    private VBox createLevelMenuItems(){

        levelVbox = new VBox(5 );
        levelVbox.setAlignment(Pos.TOP_CENTER);

        Theme theme = null;
        try {
            theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        }
        catch(Exception e) {
            System.out.println("Theme not found / corrupt file");
        }

        for (String path : UsefulMethods.scanLevelDirectory()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/level/"+path));
            Level level = LevelFactory.importLevel("src/json/level/"+path);
            ImageView snapshotview =  new ImageView(snapshot);
            String levelText = "Medaillen: "+ this.getMedalImage(level.getJsonPath());

            LevelItem levelItem = new LevelItem( new Label(level.getName()),levelText,snapshotview,path,widthLinks,heightLinks);
            listlevelButtons.add(levelItem);
            levelVbox.getChildren().add(levelItem);
        }

        return levelVbox;

    }

    private VBox createSavedGameItems(){

        savedGameVbox = new VBox(5);
        savedGameVbox.setAlignment(Pos.TOP_CENTER);
        Theme theme = null;

        try {
            theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        }
        catch(Exception e) {
            System.out.println("Theme not found / corrupt file");
        }

        for (String path : UsefulMethods.scanSavegameDirectory()) {
            Image snapShot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/savegame/"+path));
            Level level = LevelFactory.importLevel("src/json/savegame/"+path);
            ImageView snapShotView = new ImageView(snapShot);
            savedGameVbox.getChildren().add(0, new LevelItem(new Label(level.getName()),null,snapShotView,path,widthLinks,heightLinks));
        }

        return levelVbox;
    }


    /**
     * @param path Level path
     * @return Medals formatted as String
     */
    private String getMedalImage(String path) {

        Map<String,MedalStatus> medalStatuses = this.menuView.getMedalStatuses();
        MedalStatus medalStatus = medalStatuses.get(path);

        String medals = "Noch keine";
        List<ImageView> medalsList = new ArrayList();

        if (medalStatus != null) {
            for(Medal medal: Medal.values())
            if (medalStatus.has(medal)) {
                medalsList.add(new ImageView(medal.getMedalImage()));
            }
        }
        return medals;
    }

    private VBox createHelpMenuItem(){
        Image keyboardImage = new Image ("view/images/tastatur.png");
        ImageView keyboardImg= new ImageView(keyboardImage);


        String instructions =
                "\nThe official Boulder Dash games started in 1984 with the original home computer title,\n" +
                        " and continue to be published by First Star Software, Inc. \n" +
                        "\nThe game's protagonist is called 'Rockford' \n" +
                        "\n He must dig through caves collecting gems and diamonds and reach the exit  \n" +
                        "\nwithin a time limit, while avoiding various types of dangerous creatures\n" +
                        "\n\n" ;

        String shortcuts =
                "\nPress ESC  to get Back to the Menu\n" +
                        "\nPress UP, RIGHT , DOWN , Left to move \n" +
                        "\nPress W , A , D, S, to move your Map\n" +
                        "\nPress Space to Pause the Game \n" +
                        "\nWith shift you can move objects\n" +
                        "\nund so weiter\n" ;
        Label escape = new Label(shortcuts);
        Label instruction = new Label(instructions);
        escape.setFont(header);
        instruction.setFont(FONT);
      //  LevelItem helptest = new LevelItem("shortcut","anhalten",keyboardImg," ");
        helpVbox = new VBox(instruction,keyboardImg,escape);
        helpVbox.setAlignment(Pos.CENTER);
        helpVbox.setTranslateX(widthLinks/8);
        helpVbox.setTranslateY(100);


        return helpVbox;
    }

    public Button getGameButton() {
        return gameButton;
    }

    public Button getThemeEditorButton() {
        return themeEditorButton;
    }

    public Button getLevelEditorButton() {return levelEditorButton;}

    public Button getSaveButton() {return this.saveButton;}

    public VBox getMenuVboxlinks() {return this.menuVboxlinks; }




} // ende class content frame

