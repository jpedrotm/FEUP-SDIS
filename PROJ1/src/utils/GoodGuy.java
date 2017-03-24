package utils;


import java.util.Random;

public class GoodGuy {

    public static long sleepTime(int lower, int upper) {
        Random r = new Random();
        return r.nextInt(upper - lower) + lower;
    }
}
