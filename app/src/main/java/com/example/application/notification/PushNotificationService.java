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
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.async.AddNotificationRequestTask;
import com.example.application.data.NotificationData;
import com.example.application.model.User;
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
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;
import java.util.concurrent.ExecutionException;

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
        try {
            sendNotification(map);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendNotification(Map<String, String> map) throws ExecutionException, InterruptedException {
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
        double distance = Utils.getLocation(task, point);
        if (distance >= MESSAGE_SENDING_RADIUS)
            return;
        String userId = map.get("userId");
        String notificationId = map.get("notificationId");
        NotificationData notificationData = new NotificationData(userId, notificationId);

        AddNotificationRequestTask requestTask = new AddNotificationRequestTask(notificationData);
        requestTask.execute();
        Call<Integer> call = requestTask.get();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                sendMessage(event, distance);
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
            }
        });
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendMessage(String event, double distance) {
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, getClass()).addFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, getClass()).addFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

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
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
