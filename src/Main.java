
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        View view = new View(null,primaryStage);
        view.drawMap(createTestMap());
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private Feld[][] createTestMap(){
        Feld[][] result = new Feld[4][4];

        result[0][0] = new Feld(Token.ME, null) ;
        result[0][1] = new Feld(Token.MUD, null) ;
        result[0][2] = new Feld(Token.MUD, null) ;
        result[0][3] = new Feld(Token.MUD, null) ;
        result[1][0] = new Feld(Token.WALL, null) ;
        result[1][1] = new Feld(Token.SLIME, null) ;
        result[1][2] = new Feld(Token.STONE, null) ;
        result[1][3] = new Feld(Token.STONE, null) ;
        result[2][0] = new Feld(Token.PATH, null) ;
        result[2][1] = new Feld(Token.PATH, null) ;
        result[2][2] = new Feld(Token.PATH, null) ;
        result[2][3] = new Feld(Token.STONE, null) ;
        result[3][0] = new Feld(Token.STONE, null) ;
        result[3][1] = new Feld(Token. BRICKS, null) ;
        result[3][2] = new Feld(Token. BRICKS, null) ;
        result[3][3] = new Feld(Token. FIRE, null) ;

        return result;
    }
}
