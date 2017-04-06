package utils;


import java.util.Random;

public class GoodGuy {
    private static Random r = new Random();

    public static long sleepTime(int lower, int upper) {
        return r.nextInt(upper - lower) + lower;
    }
}
