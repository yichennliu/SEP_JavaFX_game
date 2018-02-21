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
    private Button continueButton;
    private Button saveButton;
    private Button restartLevel;
    private Button levelButtons;
    private Button themeEditorButton;
    private Button helpbutton;  private Button close;
    private Button levelEditorButton;
    private VBox menuBox,helpVbox;
    private ArrayList listlevelButtons;
    private Scene scene;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane,helpVboxScrollPane;
    private ScrollPane helpScrollPane;
    private double widthLinks,heightLinks ;
    private int buttonfactor=4;
    private Group root;
    private final Font FONT = Font.font("", FontWeight.BOLD, 18);


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
        this.levelEditorButton = createButton("L E V E L E D I T O R");
        setAlignment(Pos.CENTER);

        VBox menuGamePause = new VBox(15, saveButton, continueButton); // für einbindung im spiel
        menuGamePause.setVisible(false); // für einbindung im spiel

        VBox menuGameMain = new VBox(15, gameButton, levelButtons, themeEditorButton, levelEditorButton, helpbutton, close);
        VBox menuVboxlinks = new VBox(menuGamePause,menuGameMain); // für einbindung im spiel

        menuVboxlinks.setMinSize(widthLinks/2,heightLinks);
        menuVboxlinks.setId("vboxLinks");
        setHover(gameButton);
        setHover(levelButtons);
        setHover(themeEditorButton);
        setHover(helpbutton);
        setHover(close);
        setHover(saveButton);
        setHover(levelEditorButton);

      //  this.root = new Group(menuVboxlinks,menuGameMain);
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
        ScrollPane.setTranslateX(widthLinks/2-50);
        ScrollPane.setMinSize(widthLinks/2+100,heightLinks*2);
        ScrollPane.setMaxSize(widthLinks/2+50,heightLinks*2);
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
        private Button levelButton; private Text information;
        private Runnable script;


    public LevelItem(String menuItemName, String info, ImageView image, String path) {
        super(5);

        levelButton = new Button();
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
        HBox menu = new HBox(levelButton);
        menu.setAlignment(Pos.CENTER);
        VBox vboxLevelInformation= new VBox(information);
        vboxLevelInformation.setMinSize((widthLinks/2)/2,(heightLinks/2)/2);
        vboxLevelInformation.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll( vboxLevelInformation,menu,image);
        levelButton.setUserData(path);
        listlevelButtons.add(levelButton);


    }




} // end class levelItem

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
            String levelText = "Medaillen: "+ this.getMedalImage(level.getJsonPath());

            menuBox.getChildren().add( 0, new LevelItem( level.getName(),levelText,snapshotview,path));
        }

        return menuBox;

    }//ende create levele

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

    public Button getLevelEditorButton() {return levelEditorButton;}




} // ende class content frame

