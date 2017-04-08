package utils;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Lease implements Serializable {
    private Limiter limiter;
    private LeaseListener listener;

    public Lease(int maxTimestamp, LeaseListener listener) {
        this.limiter = new Limiter(maxTimestamp);
        this.listener = listener;
    }

    public void start() {
        new Timer().schedule(new TimerTask() {
            @Override
             public void run() {
                 if (limiter.limitReached()) {
                     listener.expired();
                     this.cancel();
                 }

                 limiter.tick();
             }
        }, 1000, 1000);
    }
}
