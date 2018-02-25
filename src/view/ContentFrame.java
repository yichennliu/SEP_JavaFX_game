package view;
/**
 * Created by aidabakhtiari on 09.02.18.
 */
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.LevelFactory;
import model.enums.Medal;
import model.enums.Property;
import model.game.Level;
import model.game.MedalStatus;
import model.misc.LevelSnapshot;
import model.misc.UsefulMethods;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ContentFrame extends StackPane {
    private Button themeEditorButton, loadtheme, chooseLevelButton, savedGameButton, gameButton,helpbutton,close,levelEditorButton;
    private VBox levelVbox, helpVbox, welcomeVbox, menuVboxlinks, savedGameVbox;
    private ArrayList<LevelItem> listLevelButtons;
    private ArrayList<LevelItem> listSavedGameButtons;
    private MenuView menuView;
    private ScrollPane levelItemScrollPane, helpVboxScrollPane, savedGameScrollPane;
    private double widthLinks, heightLinks;
    private int buttonfactor = 4;
    private Group root;
    private final Font FONT = Font.font("", FontWeight.BOLD, 18);
    private final Font header = Font.font("", FontWeight.BOLD, 28);
    private Text gemInfo;
    private GamePreview previewNode;


    public ContentFrame(double widthLinks, double heightLinks, MenuView menuView) {
        this.menuView = menuView;
        this.widthLinks=menuView.getWidth();
        this.heightLinks=menuView.getHeight();
        this.gameButton = createButton("S T A R T");
        this.chooseLevelButton = createButton("L E V E L S");
        this.loadtheme = createButton(" L O A D - T H E M E");
        this.themeEditorButton = createButton(" T H E M E - E D I T O R");
        this.helpbutton = createButton("H E L P");
        this.close = createButton("C L O S E ");
        this.levelEditorButton = createButton("L E V E L - E D I T O R");
        this.savedGameButton = createButton("L O A D  G A M E ");

        menuVboxlinks = new VBox(15, gameButton, chooseLevelButton, savedGameButton, loadtheme, themeEditorButton, levelEditorButton, helpbutton, close);
        menuVboxlinks.setMinSize((widthLinks / 2), heightLinks);
        menuVboxlinks.setMaxSize((widthLinks / 2), heightLinks);
        menuVboxlinks.setAlignment(Pos.CENTER);
        menuVboxlinks.setId("vboxLinks");

        this.listSavedGameButtons = new ArrayList<LevelItem>();
        this.listLevelButtons = new ArrayList<LevelItem>();

        this.levelVbox = createLevelMenuItems();
        this.levelVbox.getStyleClass().add("levelbox");
        this.levelItemScrollPane = createScrollPane(levelVbox);

        this.savedGameVbox = createSavedGameMenuItems();
        this.savedGameVbox.getStyleClass().add("levelbox");
        this.savedGameScrollPane = createScrollPane(savedGameVbox);

        this.helpVbox = createHelpMenuItem();
        this.helpVboxScrollPane = createScrollPane(helpVbox);

        this.addSavedGameVisibleButton();
        this.addHelpVisibleButton();
        this.addLevelVisibleButton();

        this.welcomeVbox = createWelcomeItem();
        this.getChildren().addAll(menuVboxlinks, welcomeVbox,levelItemScrollPane,helpVboxScrollPane, savedGameScrollPane);

    }

    private void addHelpVisibleButton(){
        this.helpbutton.setOnAction(e ->setVisible(helpVboxScrollPane));
    }


    private void addSavedGameVisibleButton(){
        this.savedGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setVisible(savedGameScrollPane);
            }
        });
    }

    private void addLevelVisibleButton(){
        this.chooseLevelButton.setOnAction(e -> setVisible(levelItemScrollPane));
    }

    public ScrollPane createScrollPane(VBox scrollVbox) {
        ScrollPane scrollPane = new ScrollPane(scrollVbox);
        scrollPane.setId("scroll");
        scrollPane.setTranslateX(widthLinks / 2);
        scrollPane.setMinSize(widthLinks / 2, heightLinks);
        scrollPane.setMaxSize(widthLinks / 2, heightLinks);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVisible(false);
        return scrollPane;
    }


    private Button createButton(String titel) {
        Button button = new Button(titel);
        button.setMinWidth(widthLinks / buttonfactor);
        button.setId("menuLeftButtons");
        return button;
    }


    private void setVisible(ScrollPane pane){
       if(pane.getParent()!=this) return;
       boolean visible = pane.isVisible();
       for(Node children:this.getChildren()){
           if(children==pane){
               if(visible) {
                   children.setVisible(false);
                   previewNode.resumeGame();
               }
               else {
                   children.setVisible(true);
                   previewNode.pauseGame();
               }
           }
           else if(children!=menuVboxlinks && children!=welcomeVbox) {
               children.setVisible(false);
           }
       }
    }


    public ArrayList<LevelItem> getListLevelButtons() {
        return listLevelButtons;
    }

    public ArrayList<LevelItem> getListSavedGameButtons() {
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
        for (String path : UsefulMethods.scanLevelDirectory()) {
            Image snapshot = LevelSnapshot.snap(theme, LevelFactory.importLevel("src/json/level/" + path));
            Level level = LevelFactory.importLevel("src/json/level/" + path);
            ImageView snapshotview = new ImageView(snapshot);
            String description ="benötigte      Edelsteine / Zeit (Sekunden)\n" +
                        "Bronze:         "+level.getGemGoals()[0]+" / "+level.getTickGoals()[0]/5 +
                          "\nSilber:           "+level.getGemGoals()[1]+" / "+level.getTickGoals()[1]/5
                    +"\nGold:             "+level.getGemGoals()[2]+" / "+level.getTickGoals()[2]/5;

            LevelItem levelItem = new LevelItem(level.getName(), "",description, snapshotview, path, widthLinks/2-100,
                    heightLinks/5, this.getMedalImage(level.getJsonPath()));
            listLevelButtons.add(levelItem);
            levelVbox.getChildren().add(levelItem);
        }
        return levelVbox;
    }

    private VBox createSavedGameMenuItems() {
        savedGameVbox = new VBox(5);
        savedGameVbox.setAlignment(Pos.TOP_CENTER);
        Theme theme = null;
        try {
            theme = ThemeIO.importTheme("src/json/theme/testTheme.zip");
        } catch (Exception e) {
            System.out.println("Theme not found / corrupt file");
        }
        for (String path : UsefulMethods.scanSavegameDirectory()) {
            Level level = LevelFactory.importLevel("src/json/savegame/" + path);
            Image snapshot = LevelSnapshot.snap(theme, level);
            ImageView snapshotview = new ImageView(snapshot);
            String description ="Fortschritt:\n" +
                    "Edelsteine: "+level.getPropertyValue(Property.GEMS) +
                    "\nZeit: "+(level.getPropertyValue(Property.TICKS)/5) + " Sekunden";
            LevelItem savedLevelItem = new LevelItem(level.getName(),"",description, snapshotview, path,
                    widthLinks/2-100, heightLinks/5, new ArrayList<>());
            listSavedGameButtons.add(savedLevelItem);
            savedGameVbox.getChildren().add(savedLevelItem);
        }

        return savedGameVbox;
    }

    private VBox createWelcomeItem(){
        Level previewLevel = LevelFactory.importLevel("src/json/level/menu.json");
        double size = widthLinks/2.5;
        double fieldSize = size/previewLevel.getWidth();

        previewNode = new GamePreview(size,size,fieldSize);
        previewNode.playLevel(previewLevel);

        welcomeVbox= new VBox();
        welcomeVbox.setTranslateX(widthLinks / 2);
        welcomeVbox.setMinSize(widthLinks / 2, heightLinks);
        welcomeVbox.setMaxSize(widthLinks / 2, heightLinks);
        welcomeVbox.getStyleClass().add("backgroundcolorBlue");
        welcomeVbox.getChildren().add(previewNode);
        welcomeVbox.setAlignment(Pos.CENTER);
        return welcomeVbox;
    }
    /**
     * @param path Level path
     * @return Medals formatted as String
     */
    private List<ImageView> getMedalImage(String path) {
        Map<String, MedalStatus> medalStatuses = this.menuView.getMedalStatuses();
        MedalStatus medalStatus = medalStatuses.get(path);
        List<ImageView> medalsList = new ArrayList();
        if (medalStatus != null) {
            for (Medal medal : Medal.values())
                if (medalStatus.has(medal)) {
                    medalsList.add(new ImageView(medal.getMedalImage()));
                }
        }
        return medalsList;
    }

    private Text createGemInfo(){
        return gemInfo;
    }

    private VBox createHelpMenuItem() {
        Image digImg = new Image("view/images/tastatur/dig.png");
        Image kiImg = new Image("view/images/tastatur/ki.png");
        Image moveImg = new Image("view/images/tastatur/move.png");
        Image scapeImg = new Image("view/images/tastatur/scape.png");
        Image spaceImg = new Image("view/images/tastatur/space.png");
        //Image muteImg = new Image("view/images/tastatur/mute.png");
        ImageView keyboardImgAll2 = new ImageView();
        String instructions =
                "\nThe official Boulder Dash games started in 1984 with the original home computer \n" +
                        "\nThe game's protagonist is called 'Rockford' \n" +
                        "\n He must dig through caves collecting gems and diamonds and reach the exit  \n" +
                        "\nwithin a time limit, while avoiding various types of dangerous creatures\n" +
                        "\n\n";
        Label instruction = new Label(instructions);
        instruction.setFont(FONT);
        LevelItem move = new LevelItem("", " \n  press   "+ "       U P , R I G H T , D O W N , L E F T   \n"+"     to  "+"           M O V E  ",
                null, new ImageView(moveImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());
        LevelItem escape = new LevelItem("", " \n  press   "+"      E S C  \n"+"    to  "+"         M E N U",
                null, new ImageView(scapeImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());
        LevelItem pause = new LevelItem(" ", " \n  press   "+"      S P A C E \n"+"     to  "+"              P A U S E",
                null, new ImageView(spaceImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());
        LevelItem shift = new LevelItem(" ", " \n  press   "+"      S H I F T  + A R R O W \n"+"    to  "+"             D I G  ",
                null, new ImageView(digImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());
        LevelItem ki = new LevelItem(" ", " \n  press   "+"                K  / ANY OTHER KEY  \n"+"   For activating  "+"    A I "+" \n  press   "+"                A N Y  O T H E R  K E Y   "+"  For activating / deactivating  ",
                null, new ImageView(kiImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());
        //LevelItem mute = new LevelItem("", " \n  press   "+"             M  / N  \n"+"   to  "+"      M U T E / P L A Y "+" \n  press   "+"      N  to  P L A Y\n",
        //        null, new ImageView(muteImg), " ", widthLinks/2-100, heightLinks/7, new ArrayList<>());

        helpVbox = new VBox(15,instruction,escape, move,pause,shift,ki/*,mute*/);
        helpVbox.setAlignment(Pos.CENTER);
        return helpVbox;
    }


    public Button getGameButton() {
        return this.gameButton;
    }
    public Button getThemeEditorButton() {
        return this.themeEditorButton;
    }
    public Button getLevelEditorButton() {
        return this.levelEditorButton;
    }
    public Button getClose() {
        return this.close;
    }
}