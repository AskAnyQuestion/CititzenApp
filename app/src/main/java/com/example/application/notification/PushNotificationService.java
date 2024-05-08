package com.example.application.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.activity.HomeActivity;
import com.example.application.model.User;
import com.example.application.retrofit.NotificationAPI;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yandex.mapkit.geometry.Point;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PushNotificationService extends FirebaseMessagingService {
    private final double MESSAGE_SENDING_RADIUS = 1;

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        SharedPreferences preferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long phone = preferences.getLong("phone", 0);
        String login = preferences.getString("login", null);
        String password = preferences.getString("password", null);
        User user = new User(phone, login, password, token);
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.userUpdate(user);
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage message) {
        Map<String, String> map = message.getData();
        super.onMessageReceived(message);
        sendNotification(map);
    }

    private void sendNotification(Map<String, String> map) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String latitude = map.get("latitude");
        String longitude = map.get("longitude");
        String event = map.get("event");
        assert latitude != null;
        assert longitude != null;
        double dLatitude = Double.parseDouble(latitude);
        double dLongitude = Double.parseDouble(longitude);

        Point point = new Point(dLatitude, dLongitude);
        Task<Location> task = fusedLocationClient.getLastLocation();
        double distance = getLocation(task, point);
        if (distance >= MESSAGE_SENDING_RADIUS)
            return;
        String userId = map.get("userId");
        String notificationId = map.get("notificationId");
        RetrofitService retrofitService = new RetrofitService();
        NotificationAPI notificationAPI = retrofitService.getRetrofit().create(NotificationAPI.class);
        Call<Integer> call = notificationAPI.add(userId, notificationId);
        sendMessage(event, distance);
    }

    private double getLocation(Task<Location> task, Point point) {
        AtomicReference<Double> atomic = new AtomicReference<>((double) 0);
        task.addOnSuccessListener(location -> {
            Point p = new Point(location.getLatitude(), location.getLongitude());
            atomic.set(Utils.calculateDistanceBetweenPoints(point, p));
        });
        return atomic.get();
    }

    private void sendMessage(String event, double distance) {
        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.putExtra("notification", true);

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
                        .addLine(event)
                        .addLine(distance + " км. от вас"))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
