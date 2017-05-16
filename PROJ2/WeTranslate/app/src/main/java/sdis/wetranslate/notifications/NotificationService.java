package sdis.wetranslate.notifications;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sdis.wetranslate.LoginActivity;
import sdis.wetranslate.R;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("amizade");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            ServerSocket serverSocket = new ServerSocket(9500);

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());

                byte[] bytes = new byte[1024];
                input.read(bytes);
                System.out.println(new String(bytes));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void notifyClient() {
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
