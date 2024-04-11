package com.example.application.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.*;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.application.R;
import com.example.application.fragments.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.*;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, LocationListener {
    public String MAPKIT_API_KEY;
    private MapView mapView;
    private Uri imageUri;
    private String text;
    private FloatingActionButton buttonCrossAdd;
    private BottomNavigationView bottomNavigationView;
    private LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);
            this.MAPKIT_API_KEY = properties.getProperty("API_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!hasUri())
            MapKitFactory.setApiKey(MAPKIT_API_KEY);

        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        init();
        buttonCrossAdd.setOnClickListener(v -> { // cross
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new IncidentFragment()).commit();
        });

        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            updateLocation(new Point(latitude, longitude));
                        }
                    });
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        updateLocation(new Point(latitude, longitude));
    }

    private void updateLocation (Point point) {
        CameraPosition position = new CameraPosition(point, 17, 0, 0);
        Map map = mapView.getMapWindow().getMap();
        map.setNightModeEnabled(true);
        map.setRotateGesturesEnabled(false);
        map.move(position, new Animation(Animation.Type.SMOOTH, 1f), null);

        UserLocationLayer locationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
        locationLayer.setVisible(true);
        locationLayer.setHeadingEnabled(true);
        if (hasUri()) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            assert finalBitmap != null;
            mapView.getMapWindow().getMap().getMapObjects().addPlacemark(object -> {
                object.setGeometry(position.getTarget());
                object.setIcon(ImageProvider.fromBitmap(finalBitmap), getIconStyle());
                object.setText(text, getText());
            });
        }
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }


    public IconStyle getIconStyle() {
        IconStyle style = new IconStyle();
        style.setRotationType(RotationType.NO_ROTATION);
        style.setScale(0.5f);
        style.setFlat(true);
        style.setZIndex(1f);
        return style;
    }

    public TextStyle getText() {
        TextStyle textStyle = new TextStyle();
        textStyle.setPlacement(TextStyle.Placement.BOTTOM);
        textStyle.setColor(Color.RED);
        textStyle.setOffset(5);
        textStyle.setSize(8);
        return textStyle;
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        buttonCrossAdd = findViewById(R.id.addCross);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false); // Неактивная зона
        mapView = findViewById(R.id.mapview);
    }

    public boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 122);
            return false;
        } else
            return true;
    }


    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        super.onStart();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                return true;
            }
            case R.id.profile -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
                return true;
            }
            case R.id.news -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new NewsFragment()).commit();
                return true;
            }
            case R.id.alarm -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new AlarmFragment()).commit();
                return true;
            }
        }
        return false;
    }

    private boolean hasUri() {
        String uri = getIntent().getStringExtra("imageUri");
        String text = getIntent().getStringExtra("text");
        if (uri != null && text != null) {
            this.imageUri = Uri.parse(uri);
            this.text = text;
            return true;
        } else
            return false;
    }
}