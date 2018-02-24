package model.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
        Iterator<String> it = savegameDir.iterator();
        while(it.hasNext()){
            String path = it.next();
            String extension = path.substring(path.lastIndexOf("."));
            if(!extension.equals(".json")) it.remove();
        }
        return savegameDir;
    }

    private static List<String> scanDir(String path){
        List<String> returnList = new ArrayList<>();
        for(String filename : (new File(path)).list()){
            returnList.add(filename);
        }
        return returnList;
    }
}
