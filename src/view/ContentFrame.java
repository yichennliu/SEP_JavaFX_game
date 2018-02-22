package view;

/**
 * Created by aidabakhtiari on 09.02.18.
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private Button saveButton;
    private Button levelButtons;
    private Button themeEditorButton;
    private Button helpbutton;  private Button close;
    private Button levelEditorButton;
    private Boolean showSavebutton;
    private VBox levelVbox,helpVbox,savedGameVbox;
    private ArrayList listlevelButtons;
    private ArrayList listSavedGameButtons;
    private Scene scene;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane,savedGameScrollPane, helpVboxScrollPane;
    private ScrollPane helpScrollPane;
    private VBox menuVboxlinks;
    private double widthLinks,heightLinks ;
    private int buttonfactor=4;
    private Group root;
    private final Font FONT = Font.font("", FontWeight.BOLD, 18);


    public Button getLevelButtons() {
        return levelButtons;
    }

    public Button getClose() {
        return close;
    }

    public Boolean getShowSavebutton() {
        return showSavebutton;
    }


    public ContentFrame(double widthLinks, double heightLinks, MenuView menuView) {

        this.widthLinks=widthLinks;
        this.heightLinks=heightLinks;
        this.menuView = menuView;
        this.gameButton = createButton("S T A R T");
        this.levelButtons = createButton("L E V E L S   ");
        this.themeEditorButton = createButton(" L O A D T H E M E ");
        this.helpbutton = createButton("H E L P");
        this.close = createButton("C L O S E ");
        this.levelEditorButton = createButton("L E V E L E D I T O R");
        this.saveButton = createButton("S A V E D  G A M E ");
        this.setAlignment(Pos.CENTER);
        this.menuVboxlinks = new VBox(15, gameButton, levelButtons, themeEditorButton, levelEditorButton, helpbutton, close);
        this.menuVboxlinks.setMinSize(widthLinks/2,heightLinks);
        this.menuVboxlinks.setId("vboxLinks");
        this.setHover(gameButton);
        this.setHover(levelButtons);
        this.setHover(themeEditorButton);
        this.setHover(helpbutton);
        this.setHover(close);
        this.setHover(levelEditorButton);
        this.setHover(saveButton);

        this.scene= new Scene(menuVboxlinks);
        this.listlevelButtons = new ArrayList<Button>();
        this.listSavedGameButtons = new ArrayList<Button>();
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
                    levelItemScrollPane.setVisible(false);
                }
            }

        });

        GamePreview preview = new GamePreview(300,300,10);

        this.getChildren().addAll(menuVboxlinks, preview);
        preview.playLevel(LevelFactory.importLevel("src/json/level/bewegung.json"));
    }


    public ScrollPane createscrollPane(VBox scrollVbox){
        ScrollPane ScrollPane = new ScrollPane(scrollVbox);
        ScrollPane.setId("scroll");
        ScrollPane.setTranslateX(widthLinks/2-50);
        ScrollPane.setMinSize(widthLinks/2+100,heightLinks*2);
        ScrollPane.setMaxSize(widthLinks/2+50,heightLinks*2);
        ScrollPane.setVisible(false);
        return ScrollPane;
    }


    private String[] scanLevelDirectory() {
        File dir = new File("src/json/level");
        return dir.list();
    }

    private String[] scanSavedGameDirectory(){
        File dir = new File("src/json/savegame");
        return dir.list();
    }

    public Button createButton(String titel ) {
       Button button= new Button(titel);
        button.setMinWidth(widthLinks/buttonfactor);
        return button;
    }

    public LevelItem getMenuItem(int index) {
        return (LevelItem) levelVbox.getChildren().get(index);
    }


    public ArrayList getListlevelButtons() {
        return listlevelButtons;
    }


    public ArrayList getListSavedGameButtons(){
        return listSavedGameButtons;
    }

    private class LevelItem extends HBox {
        private Button levelButton; private Text information;
        private Button saveButton;
        private Runnable script;


    public LevelItem(String menuItemName, String info, ImageView image, String path) {
        super(5);

        levelButton = new Button();
        saveButton = new Button();
        Text Levelname = new Text();
        Levelname.setText(menuItemName);
        InnerShadow is = new InnerShadow();
        is.setOffsetX(4.0f);
        is.setOffsetY(4.0f);
        information = new Text(info);
        information.setEffect(new GaussianBlur(1));

        levelButton.setFont(FONT);
        levelButton.setMinSize((widthLinks/2)/2,(widthLinks/2)/2);
        levelButton.setMaxSize((widthLinks/4),(widthLinks/4));
        levelButton.setTranslateY(30);
        levelButton.setGraphic(image);
        levelButton.setText(menuItemName);
        levelButton.setContentDisplay(ContentDisplay.CENTER);

        image.setFitHeight(widthLinks/4-20);
        image.setFitWidth(widthLinks/4-20);
        HBox menu = new HBox(levelButton,saveButton);
        menu.setAlignment(Pos.CENTER);
        VBox vboxLevelInformation= new VBox(information);
        vboxLevelInformation.setMinSize((widthLinks/2)/2,(heightLinks/2)/2);
        vboxLevelInformation.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(vboxLevelInformation,menu,image);
        levelButton.setUserData(path);
        saveButton.setUserData(path);
        listlevelButtons.add(levelButton);
        listSavedGameButtons.add(saveButton);

    }

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

        for (String path : scanLevelDirectory()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/level/"+path));
            Level level = LevelFactory.importLevel("src/json/level/"+path);
            ImageView snapshotview =  new ImageView(snapshot);
            String levelText = "Medaillen: "+ this.getMedalImage(level.getJsonPath());

            levelVbox.getChildren().add( 0, new LevelItem(level.getName(),levelText,snapshotview,path));
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

        for (String path : scanSavedGameDirectory()) {
            Image snapShot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/savegame/"+path));
            Level level = LevelFactory.importLevel("src/json/savegame/"+path);
            ImageView snapShotView = new ImageView(snapShot);
            savedGameVbox.getChildren().add(0, new LevelItem(level.getName(),null,snapShotView,path));
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
        escape.setFont(FONT);
      //  LevelItem helptest = new LevelItem("shortcut","anhalten",keyboardImg," ");
        helpVbox = new VBox(instruction,keyboardImg,escape);
        helpVbox.setAlignment(Pos.CENTER);
        helpVbox.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
        helpVbox.setTranslateX(widthLinks/8);
        helpVbox.setTranslateY(100);


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

