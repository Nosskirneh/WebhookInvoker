package se.nosskirneh.webhookinvoker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class WIService extends Service {
    private final String NOTIFICATION_CHANNEL_ID = "se.nosskirneh.webhookinvoker.notification";
    private final String NOTIFICATION_CHANNEL_LABEL = "WebhookInvoker";
    private NotificationManager notificationManager;
    private EventReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Toast.makeText(this.getApplicationContext(), "Started service",
                Toast.LENGTH_SHORT).show();

        receiver = new EventReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, intentFilter);

        final Intent notificationIntent = new Intent(this, WIService.class);
        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(makeChannel());
        }

        final Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(NOTIFICATION_CHANNEL_LABEL)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(pendingIntent)
                    .build();

        // Notification ID cannot be 0
        startForeground(2, notification);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel makeChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_LABEL, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(NOTIFICATION_CHANNEL_LABEL);
        notificationChannel.enableVibration(false);
        notificationChannel.setSound(null,null);
        return notificationChannel;
    }
}