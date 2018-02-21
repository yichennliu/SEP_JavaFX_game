package model.misc;

public class UsefullMethods {

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
}
