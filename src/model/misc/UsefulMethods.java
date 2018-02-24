package model.misc;

import java.io.File;
import java.util.Arrays;
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
        return scanDir("src/json/savegame/");
    }

    private static List<String> scanDir(String path){
        return Arrays.asList(new File(path).list());
    }
}
