package utils;

public class Limiter {
    private int maxTries;
    private int currentTry;

    public Limiter(int maxTries) {
        this.maxTries = maxTries;
        this.currentTry = 1;
    }

    public void tick() {
        currentTry++;
    }

    public void untick() {
        currentTry--;
    }

    public boolean limitReached() {
        return currentTry >= maxTries;
    }

    public int getCurrentTry() {
        return currentTry;
    }
}
