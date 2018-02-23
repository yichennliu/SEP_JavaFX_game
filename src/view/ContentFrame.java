package view;

/**
 * Created by aidabakhtiari on 09.02.18.
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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


public class ContentFrame extends StackPane {


    private Button themeEditorButton, loadtheme, levelButtons, saveButton, gameButton;
    private Button helpbutton;
    private Button close;
    private Button levelEditorButton;
    private Boolean showSavebutton;
    private VBox levelVbox, helpVbox, welcomeVbox, menuVboxlinks;
    private ArrayList<LevelItem> listlevelButtons;
    private ArrayList<Button> listSavedGameButtons;
    private Scene scene;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane, helpVboxScrollPane;
    private double widthLinks, heightLinks;
    private int buttonfactor = 4;
    private Group root;
    private final Font FONT = Font.font("", FontWeight.BOLD, 18);
    private final Font header = Font.font("", FontWeight.BOLD, 28);


    public ContentFrame(double widthLinks, double heightLinks, MenuView menuView) {
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
        menuVboxlinks = new VBox(15, gameButton, levelButtons, loadtheme, themeEditorButton, levelEditorButton, helpbutton, close);
        menuVboxlinks.setMinSize((widthLinks / 2), heightLinks);
        menuVboxlinks.setMaxSize((widthLinks / 2), heightLinks);
        menuVboxlinks.setAlignment(Pos.CENTER);
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

                } else {
                    helpVboxScrollPane.setVisible(true);
                    levelItemScrollPane.setVisible(false);
                }
            }

        });

       getChildren().addAll(menuVboxlinks, levelItemScrollPane,helpVboxScrollPane);
    }





    public ScrollPane createscrollPane(VBox scrollVbox) {
        ScrollPane scrollPane = new ScrollPane(scrollVbox);
        scrollPane.setId("scroll");
        scrollPane.setTranslateX(widthLinks / 2);
        scrollPane.setMinSize(widthLinks / 2, heightLinks);
        scrollPane.setMaxSize(widthLinks / 2, heightLinks);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVisible(false);
        return scrollPane;
    }


    private String[] scanLevelDirectory() {
        File dir = new File("src/json/level");
        return dir.list();
    }

    private String[] scanSavedGameDirectory() {
        File dir = new File("src/json/savegame");
        return dir.list();
    }

    public Button createButton(String titel) {
        Button button = new Button(titel);
        button.setMinWidth(widthLinks / buttonfactor);
       button.setId("menuLeftButtons");
        return button;
    }

    public ArrayList<LevelItem> getListlevelButtons() {
        return listlevelButtons;
    }

    public ArrayList getListSavedGameButtons() {
        return listSavedGameButtons;
    }

    private VBox createLevelMenuItems() {

        levelVbox = new VBox(5);
        levelVbox.setAlignment(Pos.TOP_CENTER);

        Theme theme = null;
        try {
            theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        } catch (Exception e) {
            System.out.println("Theme not found / corrupt file");
        }

        for (String path : scanLevelDirectory()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/level/" + path));
            Level level = LevelFactory.importLevel("src/json/level/" + path);
            ImageView snapshotview = new ImageView(snapshot);
            String levelText = "Medaillen: " + this.getMedalImage(level.getJsonPath());

            LevelItem levelItem = new LevelItem(level.getName(), levelText, snapshotview, path, widthLinks/2-100, heightLinks/5);
            listlevelButtons.add(levelItem);
            levelVbox.getChildren().add(levelItem);
        }

        return levelVbox;

    }


    private VBox createWelcomeItem(){
        welcomeVbox= new VBox();
        welcomeVbox.setTranslateX(widthLinks / 2);
        welcomeVbox.setMinSize(widthLinks / 2, heightLinks);
        welcomeVbox.setMaxSize(widthLinks / 2, heightLinks);
        welcomeVbox.getStyleClass().add("backgroundcolorBlue");
        return welcomeVbox;
    }

    /**
     * @param path Level path
     * @return Medals formatted as String
     */
    private String getMedalImage(String path) {

        Map<String, MedalStatus> medalStatuses = this.menuView.getMedalStatuses();
        MedalStatus medalStatus = medalStatuses.get(path);

        String medals = "Noch keine";
        List<ImageView> medalsList = new ArrayList();

        if (medalStatus != null) {
            for (Medal medal : Medal.values())
                if (medalStatus.has(medal)) {
                    medalsList.add(new ImageView(medal.getMedalImage()));
                }
        }
        return medals;
    }

    private VBox createHelpMenuItem() {
        Image digImg = new Image("view/images/tastatur/dig.png");
        Image kiImg = new Image("view/images/tastatur/ki.png");
          Image moveImg = new Image("view/images/tastatur/move.png");
          Image scapeImg = new Image("view/images/tastatur/scape.png");
          Image spaceImg = new Image("view/images/tastatur/space.png");

        ImageView keyboardImgAll2 = new ImageView();


        String instructions =
                "\nThe official Boulder Dash games started in 1984 with the original home computer \n" +
                        "\nThe game's protagonist is called 'Rockford' \n" +
                        "\n He must dig through caves collecting gems and diamonds and reach the exit  \n" +
                        "\nwithin a time limit, while avoiding various types of dangerous creatures\n" +
                        "\n\n";

        Label instruction = new Label(instructions);
        instruction.setFont(FONT);
        LevelItem move = new LevelItem(" \n  press   "+ "       U P , R I G H T , D O W N , L E F T   \n"+"     to  "+"           M O V E  ", "  ",new ImageView(moveImg), " ", widthLinks/2-100, heightLinks/7);
        LevelItem escape = new LevelItem(" \n  press   "+"      E S C  \n"+"    to  "+"         M E N U", " ", new ImageView(scapeImg), " ", widthLinks/2-100, heightLinks/7);
         LevelItem pause = new LevelItem(" \n  press   "+"      S P A C E \n"+"     to  "+"              P A U S E", " ", new ImageView(digImg), " ", widthLinks/2-100, heightLinks/7);
         LevelItem shift = new LevelItem(" \n  press   "+"      S H I F T  + A R R O W \n"+"    to  "+"             D I G  ", "  ", new ImageView(spaceImg), " ", widthLinks/2-100, heightLinks/7);
         LevelItem ki = new LevelItem(" \n  press   "+"             K   \n"+"   For activating  "+"      A I "+" \n  press   "+"                A N Y  O T H E R  K E Y   "+"  For deactivating  ", " ", new ImageView(kiImg), " ", widthLinks/2-100, heightLinks/7);
        helpVbox = new VBox(15,instruction,escape, move,pause,shift,ki);
        helpVbox.setAlignment(Pos.CENTER);

        return helpVbox;
    }


    public Button getGameButton() {
        return gameButton;
    }

    public Button getThemeEditorButton() {
        return themeEditorButton;
    }

    public Button getLevelEditorButton() {
        return levelEditorButton;
    }


    public Button getSaveButton() {
        return this.saveButton;
    }

    public VBox getMenuVboxlinks() {
        return this.menuVboxlinks;
    }

    public Button getLevelButtons() {
        return levelButtons;
    }

    public Button getClose() {
        return close;
    }

    public Boolean getShowSavebutton() {
        return showSavebutton;
    }


} // ende class content frame

