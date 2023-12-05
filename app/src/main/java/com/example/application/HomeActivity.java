package com.example.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.TextStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public String MAPKIT_API_KEY;
    private MapView mapView;
    private Uri imageUri;
    private String text;
    BottomNavigationView bottomNavigationView;

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
        if (!hasUri()) {
            MapKitFactory.setApiKey(MAPKIT_API_KEY);
        }
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        init();

        CameraPosition position = new CameraPosition(new Point(57.00133883199114, 40.94248032495209),
                14, 0, 0);
        mapView.getMap().setNightModeEnabled(true);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(position, new Animation(Animation.Type.SMOOTH, 1f), null);
        if (requestLocationPermission()) {
            MapKit mapKit = MapKitFactory.getInstance();
            UserLocationLayer locationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
            locationLayer.setVisible(true);
            locationLayer.setHeadingEnabled(true);
        }
        if (hasUri()) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap finalBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            assert finalBitmap != null;
            mapView.getMap().getMapObjects().addPlacemark(object -> {
                object.setGeometry(position.getTarget());
                object.setIcon(ImageProvider.fromBitmap(finalBitmap), getIconStyle());
                object.setText(text, getText());
            });
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
        mapView = findViewById(R.id.mapview);
    }

    private boolean requestLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED);
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
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new HomeFragment())
                        .commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new ProfileFragment())
                        .commit();
                return true;

            case R.id.add:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new IncidentFragment())
                        .commit();
                return true;

            case R.id.news:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new NewsFragment())
                        .commit();
                return true;

            case R.id.alarm:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, new AlarmFragment())
                        .commit();
                return true;
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