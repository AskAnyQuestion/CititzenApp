package com.example.application.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.provider.Settings;
import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.application.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage message) {
        super.onMessageReceived(message);
        System.out.println(FirebaseMessaging.getInstance().getToken().getResult());
        getFirebaseMessage(Objects.requireNonNull(message.getNotification()).getTitle(),
                message.getNotification().getBody());
    }

    private void getFirebaseMessage(String title, String body) {
        final String CHANNEL_ID = "CITIZEN_ID_CHANNEL";
        final String CHANNEL_NAME = "CITIZEN_CHANNEL";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(new SpannableString("Рядом происшествие"))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText(title + " " + body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }

    /*
    private void sendNotification(IncidentMap data) {
        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.putExtra("notification", true);

        double distance = Utils.calculateDistanceBetweenPoints(position.getTarget(), data.getPoint());
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final String CHANNEL_ID = "CITIZEN_ID_CHANNEL";
        final String CHANNEL_NAME = "CITIZEN_CHANNEL";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(new SpannableString("Рядом происшествие"))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(data.getDescription())
                        .addLine(distance + " км. от вас"))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
    */
}
