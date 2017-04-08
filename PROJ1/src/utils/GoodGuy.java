package utils;


import java.util.Random;

public class GoodGuy {
    private static Random r = new Random();

    public static long randomBetween(int lower, int upper) {
        return r.nextInt(upper - lower) + lower;
    }

    public static void sleepRandomTime(int lower, int upper) {
        try {
            Thread.sleep(randomBetween(lower, upper));
        } catch (InterruptedException e) {}
    }
}
