package view;

/**
 * Created by aidabakhtiari on 09.02.18.
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.LevelFactory;
import model.game.Level;
import model.misc.LevelSnapshot;
import model.themeEditor.ThemeIO;

import java.io.File;
import java.util.ArrayList;


public  class ContentFrame extends StackPane {

    public Button gameButton;
    private Button helpButton;
    private Button themeEditorButton;
    private Button test;
    private int currentItem = 0;
    private boolean  showMenuItems= false;
    private VBox menuBox;
    private ArrayList listlevelButtons;
    private Scene scene;
    private MenuView menuView;
    ScrollPane scroll ;



    public ContentFrame() {
        this.gameButton = createContent("S T A R T");
        this.helpButton = createContent("L E V E L S   ");
        this.themeEditorButton = createContent("T H E M E ");
        gameButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
         setAlignment(Pos.CENTER);
        VBox vbButtons = new VBox(gameButton,helpButton,themeEditorButton);
        vbButtons.setStyle("-fx-background-color: #020d1e;" +
                "    -fx-padding: 100;\n" +
                "    -fx-spacing: 10;");
        vbButtons.setMinHeight(600);
        this.scene= new Scene(vbButtons);
        this.listlevelButtons = new ArrayList<Button>();
        menuBox = createLevelMenuItems();
        scroll= new ScrollPane(menuBox);
        scroll.setTranslateX(400);
        scroll.setMinSize(500,600);
        scroll.setVisible(false);
        getChildren().addAll(scroll);



        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
             if(scroll.isVisible()){
                 scroll.setVisible(false);
             }
             else scroll.setVisible(true);
            }
        });

        getChildren().addAll(vbButtons);
    }

    private String[] scanLevelDirectory() {
        File dir = new File("src/json/level");
        return dir.list();
    }


    public Button createContent(String titel ) {
       Button button= new Button(titel);
        // VBox letters = new VBox(button);
       // button.setAlignment(Pos.TOP_CENTER);
        return button;
    }


   /* public void setHelpButton() {
        helpButton.setOnAction((ActionEvent event) -> {
            Dialog<Pair<String, String>> helpWindow = new Dialog<>();
            helpWindow.setTitle("HELP INSTRUCTIONS");
            helpWindow.setHeaderText("DIESER TEXT  IST EIN DUMMIE : ");
            helpWindow.setContentText(" hier findest du Hilfe : http://code.makery.ch/blog/javafx-dialogs-official/");
            helpWindow.initStyle(StageStyle.UTILITY);
            ButtonType okButton = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
            helpWindow.getDialogPane().getButtonTypes().addAll(okButton);
            helpWindow.showAndWait();

        });
    }*/




    public Button getGameButton() {
        return gameButton;
    }

    public Button getHelpButton() {
        return helpButton;
    }

    public Button getThemeEditorButton() {
        return themeEditorButton;
    }

    public LevelItem getMenuItem(int index) {
        return (LevelItem)menuBox.getChildren().get(index);
    }


    public ArrayList getListlevelButtons() {
   System.out.println(listlevelButtons.size());
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
        HBox menu = new HBox(levelButton);
        menu.setMinSize(100,10);
        menu.setAlignment(Pos.CENTER);
        image.setFitWidth(55);
        image.setFitHeight(55);

        VBox vbox= new VBox(information);
        vbox.setMinSize(50,100);
        getChildren().addAll( vbox,menu,image);
        setActive(false);
        setOnActivate(() -> System.out.println(menuItemName + " aktiv geworden"));
        levelButton.setUserData(path);
        listlevelButtons.add(levelButton);
      //  System.out.println(listlevelButtons);

    }

    public void setActive(boolean b) {
        //levelButton.setFill(b ? Color.RED : Color.grayRgb(10,0.9));
        levelButton.setStyle("-fx-font: 12 arial; -fx-base: #b6e7c9;");
        information.setFill(b? Color.grayRgb(10,0.8) : Color.grayRgb(10,0.0) );
       // image.setVisible(b);

    }



    public void setOnActivate(Runnable r) {
        script = r;
    }

    public void activate() {
        if (script != null)
            script.run();

    }




} // ende levelitem



    public VBox showLevelMenuItems(boolean show) {

        if (!show) {
            System.out.println("das ist kliked");

            showMenuItems=true;

        }
        else {
            System.out.println("das niiiiiiicht  kliked");
            showMenuItems=false;
            scroll.setVisible(true);
            menuBox.getChildren().clear();
        }
        return menuBox;

    }

    private VBox createLevelMenuItems(){

        menuBox = new VBox(5 );
        menuBox.setMinWidth(300);
        menuBox.setAlignment(Pos.TOP_CENTER);
      //  menuBox.setTranslateX(300);
      //  menuBox.setTranslateY(60);

        for (String path : scanLevelDirectory()) {
            Level level = LevelFactory.importLevel("src/json/level/"+path);
            Image snapshot = LevelSnapshot.snap(ThemeIO.importTheme("src/json/theme/testTheme.zip"),level);
            ImageView snapshotview =  new ImageView(snapshot);

            menuBox.getChildren().add( 0, new LevelItem( level.getName(),"  ",snapshotview,path));
            snapshotview.setFitWidth(100);
            snapshotview.setFitHeight(100);
            getMenuItem(0).setActive(true);

        }


        return menuBox;

    }//ende create levele







}






//   ToggleButton levelButton = new ToggleButton("level : " + path,snapshotview );

        /*  LevelItem levelButton = new LevelItem("level : " + path, " hier kommt info",new ImageView(snapshot));
            levelButton.setUserData(path);
            levelButton.setToggleGroup(group);
            levelButton.setMinSize(200,100);
            levelButton.setMaxSize(400,200);*/




  /*

   helpButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        menuView.getSceneMenu().setOnKeyPressed(event -> {
                            System.out.println(currentItem);

                            if (event.getCode() == KeyCode.UP) {
                                if (currentItem > 0) {
                                    getMenuItem(currentItem).setActive(false);
                                    getMenuItem(--currentItem).setActive(true);
                                }
                            }

                            if (event.getCode() == KeyCode.DOWN) {
                                if (currentItem < menuBox.getChildren().size() - 1) {
                                    getMenuItem(currentItem).setActive(false);
                                    getMenuItem(++currentItem).setActive(true);
                                    System.out.println("unten");
                                }
                            }

                            if (event.getCode() == KeyCode.ENTER) {
                                System.out.println("eeeeenter");
                                getMenuItem(currentItem).activate();

                            }
                        });


                    }

                });
*/