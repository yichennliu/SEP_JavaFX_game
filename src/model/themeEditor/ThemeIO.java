package model.themeEditor;

import javafx.embed.swing.SwingFXUtils;
import model.enums.Token;
import org.json.*;
import view.Theme;
import view.Theme.FeldType;
import view.Theme.Position;
import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ThemeIO {

    public Theme importTheme(String path){
        return null;
    }

    public static void exportTheme(Theme theme) {

        HashMap2D<Token, FeldType, List<SpriteSheet>> images = theme.getImages();
        JSONObject themeObj = new JSONObject();
        JSONArray tokenSprites = new JSONArray();

        themeObj.put("name", theme.getName());
        themeObj.put("sprites", tokenSprites);

        /* Array for every Token */
        for (Token t : Token.values()) {
            boolean hasValues = false; //
            JSONObject tokenObj = new JSONObject();
            tokenObj.put("name",t.name());

            JSONArray feldTypeObjects = new JSONArray();
            tokenObj.put("feldTypeObjects",feldTypeObjects);

            /* Array for every Feldtype in Token */
            for (FeldType f : FeldType.values()) {
                List<SpriteSheet> spriteSheets = images.get(t,f);
                if(spriteSheets!=null && spriteSheets.size()>0 ){
                    if(hasValues==false) hasValues = true; // sets flag so that this token and feldtype will be added to themeObj
                    JSONObject feldTypeObject = new JSONObject();
                    feldTypeObject.put("name",f.name());
                    JSONArray variations = new JSONArray();
                    feldTypeObject.put("variations",variations);

                    for(int i = 0; i <spriteSheets.size(); i++){
                        JSONObject spriteSheetObj = new JSONObject();
                        variations.put(spriteSheetObj);

                        SpriteSheet s = spriteSheets.get(i);
                        String path = writeSpriteSheet(theme.getName(),t,f,s,i);
                        spriteSheetObj.put("spriteSize",i);
                        spriteSheetObj.put("path",path);
                    }

                    feldTypeObjects.put(feldTypeObject);
                }

            }
            if(hasValues) tokenSprites.put(tokenObj);
        }
       FileWriter fw;
        try {
           fw = new FileWriter(theme.getName()+".json");
           BufferedWriter bw = new BufferedWriter(fw);
           bw.write(themeObj.toString(4));
           bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String writeSpriteSheet(String themeName, Token t, FeldType f, SpriteSheet s, int index){
        String fs = File.separator;
        String pathString = "src/json/themes/"+themeName+"/"+t.name()+"/";
        String fileNameString = f.name()+"_"+index+".png";
        File path = new File(pathString);
        try {
            path.mkdirs();
        }
        catch(SecurityException e){
            System.out.println("Exception:  " + e.getMessage());
            e.printStackTrace();
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(s.getSpriteSheet(Position.DEFAULT), null), "png",new File(pathString+fileNameString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pathString+fileNameString;
    }
}

//
