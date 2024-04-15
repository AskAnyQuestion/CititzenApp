package com.example.application.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.Rect;
import android.location.*;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.map.IncidentData;
import com.example.application.fragments.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.ArrayList;
import java.util.Properties;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") // WeakReference
    private final ArrayList<MapObjectTapListener> listeners = new ArrayList<>();
    private String MAPKIT_API_KEY;
    private String text;
    private Uri uri;
    private MapView mapView;
    private ImageView imageIncident;
    private FrameLayout layout;
    private TextView lastUpdate;
    private TextView description;
    private TextView streetAndKm;
    private FloatingActionButton buttonCrossAdd;
    private FloatingActionButton buttonFindMe;
    private FloatingActionButton buttonChangeTime;
    private BottomNavigationView bottomNavigationView;
    private CameraPosition position;
    private UserLocationLayer locationLayer;
    private MapObjectCollection objCollection;
    private boolean isNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.MAPKIT_API_KEY = getApiToken();
        if (isFirstStart())
            MapKitFactory.setApiKey(MAPKIT_API_KEY);
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        init();
        initPreference();

        buttonCrossAdd.setOnClickListener(v -> { // cross
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new IncidentFragment()).commit();
        });
        buttonFindMe.setOnClickListener(v -> { // find
            updateLocation(position.getTarget());
        });
        buttonChangeTime.setOnClickListener(v -> { // night-mode
            Map map = this.mapView.getMapWindow().getMap();
            this.isNightMode = !isNightMode;
            map.setNightModeEnabled(this.isNightMode);
            // Preference
            SharedPreferences sharedPreferences = getApplicationContext()
                    .getSharedPreferences("NightModePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("nightMode", isNightMode);
            editor.apply();
        });

        if (isLocationPermissionGranted()) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            getLocation(fusedLocationClient);
        }
    }

    private String getApiToken() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);
            return MAPKIT_API_KEY = properties.getProperty("API_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getLocation(FusedLocationProviderClient fusedLocationClient) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(this, location -> {
            if (location != null) {
                buttonFindMe.show();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateLocation(new Point(latitude, longitude));
            } else
                buttonFindMe.hide();
        });
    }

    private void updateLocation(Point point) {
        this.position = new CameraPosition(point, 17, 0, 0);
        Map map = mapView.getMapWindow().getMap();
        map.setRotateGesturesEnabled(false);
        map.setNightModeEnabled(isNightMode);
        map.move(position, new Animation(Animation.Type.SMOOTH, 1f), null);
        this.objCollection = map.getMapObjects();

        if (locationLayer == null) {
            locationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
            locationLayer.setVisible(true);
            locationLayer.setHeadingEnabled(true);
        }
        if (!isFirstStart()) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
            Bitmap cloneBitmap = getIconBitmap(scaledBitmap);
            Bitmap bit = getRoundedCornerBitmap(cloneBitmap);

            /* Добавление инцидента */
            addIncident(bit);
            /* Просмотр инцидента */
            watchIncident();
        }
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    private void addIncident(Bitmap cloneBitmap) {
        this.objCollection.addPlacemark(object -> {
            Point p = position.getTarget();
            object.setUserData(new IncidentData(p, cloneBitmap, text, this));
            object.setGeometry(p);
            object.setIcon(ImageProvider.fromBitmap(cloneBitmap), getIconStyle());
            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout);
            bottomSheetBehavior.setPeekHeight(0, true);
            layout.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    @SuppressLint("SetTextI18n")
    private void watchIncident() {
        MapObjectTapListener listener = (mapObject, point) -> {
            IncidentData data = (IncidentData) mapObject.getUserData();
            if (data != null) {
                String lastUpdateMessage = "Последнее обновление: ";
                Point incidentPoint = data.getPoint();
                double kilometers = Utils.calculateDistanceBetweenPoints(position.getTarget(), incidentPoint);
                lastUpdate.setText(lastUpdateMessage + data.getTime());
                description.setText(data.getDescription());
                streetAndKm.setText(data.getAddress() + " - " + kilometers + " км.");
                imageIncident.setImageBitmap(data.getImage());
                BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return true;
        };
        listeners.add(listener);
        this.objCollection.addTapListener(listener);
    }
    private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, 50, 50, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    private Bitmap getIconBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth() + 40;
        int height = bitmap.getHeight() + 40;
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawRect(20, 0, width - 20, height, paint);
        canvas.drawRect(0, 20, width, height - 20, paint);
        canvas.drawRect(0, height - 20, width, height, paint);
        canvas.drawBitmap(bitmap, 20, 20, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(20, 20, width - 20, height - 20, paint);
        return outputBitmap;
    }


    private IconStyle getIconStyle() {
        IconStyle style = new IconStyle();
        style.setRotationType(RotationType.NO_ROTATION);
        style.setScale(0.4f);
        style.setZIndex(0f);
        return style;
    }

    private TextStyle getText() {
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
        buttonFindMe = findViewById(R.id.findme);
        buttonChangeTime = findViewById(R.id.changeTIme);
        lastUpdate = findViewById(R.id.lastUpdate);
        description = findViewById(R.id.description);
        streetAndKm = findViewById(R.id.streetAndKm);
        imageIncident = findViewById(R.id.imageIncident);
        layout = findViewById(R.id.frame_layout_r);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        mapView = findViewById(R.id.mapview);
    }

    private void initPreference() {
        SharedPreferences preferences = getApplicationContext().
                getSharedPreferences("NightModePrefs", Context.MODE_PRIVATE);
        this.isNightMode = preferences.getBoolean("nightMode", false);
    }

    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 122);
            return false;
        }
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

    private boolean isFirstStart() {
        Intent intent = getIntent();
        String uri = intent.getStringExtra("imageUri");
        String text = intent.getStringExtra("text");
        if (uri != null && text != null) {
            this.uri = Uri.parse(uri);
            this.text = text;
            return false;
        } else
            return true;
    }
}