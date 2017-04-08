package utils;

import java.util.Timer;
import java.util.TimerTask;

public class Lease {
    private Limiter limiter;
    private LeaseListener listener;

    public Lease(int maxTimestamp, LeaseListener listener) {
        limiter = new Limiter(maxTimestamp);
        this.listener = listener;
    }

    public void start() {
        new Timer().schedule(new TimerTask() {
            @Override
             public void run() {
                 if (limiter.limitReached()) {
                     listener.expired();
                 }

                 limiter.tick();
             }
        }, 1000, 1000);
    }
}
