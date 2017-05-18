package sdis.wetranslate.notifications;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import sdis.wetranslate.LoginActivity;
import sdis.wetranslate.R;

public class NotificationService extends IntentService {
    private WebSocketListener wsl;

    public NotificationService() {
        super("notifier");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            wsl = new WebSocketListener(new URI("ws://wetranslate.ddns.net:7001"), this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            wsl.connectBlocking();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "jbarbosa");
            jsonObject.put("key", "2BC5F503051821B37577943F717683B0AB0DB4253CD42D4D40270D480066DFEC");
            wsl.send(jsonObject.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void notifyClient() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_add_circle);
        notificationBuilder.setContentTitle("Boas");
        notificationBuilder.setContentText("tudo vem?");

        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
