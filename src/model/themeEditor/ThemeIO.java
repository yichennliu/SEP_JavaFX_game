package model.themeEditor;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.enums.Token;
import org.json.*;
import model.themeEditor.Theme.FeldType;
import model.themeEditor.Theme.Position;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ThemeIO {

    /*creates JSON String from Theme-Object and adds path (class ImagePath) to provided List*/
    public static String createJSONString(Theme theme, List<ImagePath> pathList){

        HashMap2D<Token,FeldType, SpriteSheetHolder> map = theme.getMap();
        JSONObject themeObj = new JSONObject();
        JSONArray tokenArray = new JSONArray();

        themeObj.put("name",theme.getName());
        themeObj.put("sprites", tokenArray);

        for(Token t: Token.values()){
            boolean hasValues = false;
            JSONObject token = new JSONObject();
            token.put("name",t.name());

            JSONArray feldTypeArray = new JSONArray();
            token.put("feldTypeArray",feldTypeArray);

            for(FeldType f: FeldType.values()){
                SpriteSheetHolder holder = map.get(t,f);
                if(holder!=null) {
                    hasValues = true;
                    JSONObject feldType = new JSONObject();
                    feldType.put("name",f.name());
                    JSONArray positions = new JSONArray();
                    feldType.put("positions",positions);
                    for(Position p: f.getAllowedPositions()){
                        SpriteSheet sheet = holder.getSpriteSheet(p);
                        if(sheet!=null){
                            JSONObject position = new JSONObject();
                            position.put("name",p.name());

                            JSONObject spriteSheet = new JSONObject();
                            spriteSheet.put("size",sheet.getSpriteSize());
                            spriteSheet.put("count" , sheet.getCount());
                            ImagePath path = new ImagePath(t,f,p);
                            spriteSheet.put("path" , path.toString());
                            pathList.add(path);

                            feldTypeArray.put(feldType);
                            position.put("position",spriteSheet);
                            positions.put(position);
                        }
                    }
                }
            }
            if(hasValues) tokenArray.put(token);
        }
        return themeObj.toString(4);
    }

    public static void exportTheme(Theme theme) {

        String pathString = "src/json/theme/";
        File outputFile = new File(pathString+theme.getName()+".zip");

        new File(pathString).mkdirs();

        ZipOutputStream zos;
        try {
            List<ImagePath> imagePaths = new ArrayList();
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            zos = new ZipOutputStream(bos);

            String jsonString = createJSONString(theme,imagePaths);
            ZipEntry jsonEntry = new ZipEntry("theme.json");
            zos.putNextEntry(jsonEntry);
            byte[] data  = jsonString.getBytes();
            zos.write(data,0,data.length);
            zos.closeEntry();

            saveImagesToZipStream(theme,imagePaths,zos);

            zos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void saveImagesToZipStream(Theme theme, List<ImagePath> pathList, ZipOutputStream stream){
        try {
            HashMap2D<Token, FeldType, SpriteSheetHolder> images = theme.getMap();
            for(ImagePath path : pathList){
                SpriteSheet s = images.get(path.getToken(),path.getFeldType()).getSpriteSheet(path.getPosition());
                ZipEntry imageEntry = new ZipEntry(path.toString());
                stream.putNextEntry(imageEntry);
                ImageIO.write(SwingFXUtils.fromFXImage(s.getSpriteSheet(), null), "png",stream);
                stream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*imports theme or null if theme-File does not exist */
    public static Theme importTheme(String path){
        File themeFile = new File(path);
        if(themeFile.exists()){
            return importTheme(themeFile);
        }
        return null;
    }

    /*imports theme or null if theme-File does not exist */
    public static Theme importTheme(File path){
        if (!path.exists()) return null;
        try {
            ZipFile file = new ZipFile(path);
            Map<String,Image> imageMap = new HashMap<String, Image>();
            JSONObject theme = null;

            for(Enumeration<? extends ZipEntry> enumeration = file.entries(); enumeration.hasMoreElements();){
                ZipEntry entry = enumeration.nextElement();
                String ext = getExtension(entry.getName());
                if(ext.equals("png")){
                   imageMap.put(entry.getName(),new Image(file.getInputStream(entry)));
                }
                else if(ext.equals("json")){
                    theme = new JSONObject(new JSONTokener(file.getInputStream(entry)));
                }

            }
            if(theme!=null) {
                return parseJSONToTheme(theme,imageMap);
            }
            file.close();

        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        return null;
    }

    /*returns Theme-Object or, if parsing fails, null*/
    private static Theme parseJSONToTheme(JSONObject themeObj, Map<String,Image> imageMap){
        String name = themeObj.getString("name");
        JSONArray sprites = themeObj.getJSONArray("sprites");
        Theme theme = new Theme(name);

        if(sprites!=null){
            for(int i =0; i<sprites.length();i++){
                JSONObject token = sprites.getJSONObject(i);
                Token t = Token.valueOf(token.getString("name"));

                JSONArray feldTypes = token.getJSONArray("feldTypeArray");
                if(feldTypes!=null){
                    for(int j=0; j<feldTypes.length(); j++){

                        JSONObject jsonSpriteSheetHolder = feldTypes.getJSONObject(j);
                        FeldType f = FeldType.valueOf(jsonSpriteSheetHolder.getString("name"));

                        JSONArray positions = jsonSpriteSheetHolder.getJSONArray("positions");
                        for(int k=0; k<positions.length();k++){
                            JSONObject jsonSpriteSheet = positions.getJSONObject(k);
                            Position p = Position.valueOf(jsonSpriteSheet.getString("name"));
                            JSONObject spriteSheetInfo = jsonSpriteSheet.getJSONObject("position");
                            Image spriteSheetImage = imageMap.get(spriteSheetInfo.getString("path"));
                            int spriteSize = spriteSheetInfo.getInt("size");
                            int count = spriteSheetInfo.getInt("count");
                            try {
                                SpriteSheet spriteSheet = new SpriteSheet(spriteSheetImage,spriteSize,count);
                                theme.putSpriteSheet(t,f,p,spriteSheet);
                            }
                            catch(IllegalArgumentException e){
                                System.out.println("WAAAAS?");
                            }

                        }

                    }
                }
            }
            return theme;
        }
        return null;
    }

    private static String getExtension(String path){
        if (path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
            return path.substring(path.lastIndexOf(".") + 1);
        else return "";
    }

    private static class ImagePath{
        private Token t;
        private Theme.FeldType f;
        private Position p;

        public ImagePath(Token t, FeldType f,Position p){
            this.t = t; this.f = f; this.p = p;
        }

        public Token getToken(){
            return this.t;
        }

        public FeldType getFeldType(){
            return this.f;
        }

        public Position getPosition(){
            return this.p;
        }

        public String toString(){
            return t.name()+"/"+f.name()+"/"+p.name()+".png";
        }
    }



}
//
