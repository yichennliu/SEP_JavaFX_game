package model.misc;

import model.game.MedalStatus;
import model.themeEditor.Theme;
import model.themeEditor.ThemeIO;

import java.io.File;
import java.util.*;

public class UsefulMethods {

    /*überprüft, ob b zwischen a und c ist*/
    public static boolean between(int a, int b, int c) {
        if (c > a) {
            return (b >= a && b <= c);
        }
        if (a > c) {
            return (b >= c && b <= a);
        }

        return (b == c);
    }

    public static List<String> scanLevelDirectory(){
        return scanDir("src/json/level/");
    }

    public static List<String> scanSavegameDirectory(){
        List<String> savegameDir = scanDir("src/json/savegame");
        removeFromListByExtension(".json",savegameDir);
        return savegameDir;
    }

    /*Removes all Elemnts from list that do not match provided extension*/
    private static void removeFromListByExtension(String extension, List<String> list){
        Iterator<String> it = list.iterator();
        while(it.hasNext()){
            String path = it.next();
            String currentExt = path.substring(path.lastIndexOf("."));
            if(!currentExt.equals(extension)) it.remove();
        }
    }

    private static List<String> scanDir(String path){
        List<String> returnList = new ArrayList<>();
        for(String filename : (new File(path)).list()){
            returnList.add(filename);
        }
        return returnList;
    }

    public static List<Theme> scanThemeDirectory() {
        List<String> paths = scanDir("src/json/theme");
        List<Theme> resultList = new ArrayList<>();
        if(paths!=null){
            removeFromListByExtension(".zip",paths);
        }
        for(String path: paths){
            try {
                Theme theme = ThemeIO.importTheme("src/json/theme/"+path);
                resultList.add(theme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;

    }

    public static int getPoints(Map<String, MedalStatus> medalStatuses) {
        int result =  0;
        for(Map.Entry<String,MedalStatus> entry: medalStatuses.entrySet()){
            result = result + entry.getValue().getPoints();
        }
        return result;
    }
}
