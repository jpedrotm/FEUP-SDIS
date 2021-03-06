package sdis.wetranslate.notifications;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String username=sharedPreferences.getString(LoginActivity.Username,"");
            String key=sharedPreferences.getString(LoginActivity.KeyUser,"");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("key", key);
            wsl.send(jsonObject.toString());
        } catch (Exception e) {
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
