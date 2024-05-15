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
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.adapters.ViewPagerBitmapAdapter;
import com.example.application.async.AddIncidentRequestTask;
import com.example.application.async.GetIncidentRequestTask;
import com.example.application.async.GetIncidentMaterialRequestTask;
import com.example.application.exception.SERVER;
import com.example.application.fragments.*;
import com.example.application.data.IncidentMap;
import com.example.application.model.Incident;
import com.example.application.model.User;
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
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") // WeakReference
    private final ArrayList<MapObjectTapListener> objectListener = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") // WeakReference
    private final ArrayList<InputListener> mapListener = new ArrayList<>();
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private HashMap<Integer, ArrayList<Bitmap>> hashMap;
    private ArrayList<Bitmap> bitmaps;
    private String text;
    private Map map;
    private MapObjectCollection objCollection;
    private ImageView imageIncident;
    private FrameLayout layout;
    private MapView mapView;
    private TextView city, lastUpdate, description, streetAndKm, photo;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton buttonCrossAdd, buttonFindMe, buttonChangeTime;
    private ViewPager viewPager;
    private CameraPosition position;
    private UserLocationLayer locationLayer;
    private User user;
    private DisplayMetrics metrics;
    private boolean isNightMode, isMapInit;

    private void initMap() {
        String MAPKIT_API_KEY = getApiToken();
        if (incidentCreated() || notificationCreated() || isMapInit || MAPKIT_API_KEY == null)
            return;
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.setLocale("ru_RU");
        MapKitFactory.initialize(this);
        isMapInit = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initMap();
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        initComponents();
        initMetrix();
        initIncidents();
        initPreferences();
        initListeners();
        initUserLocation();
    }

    private void initUserLocation() {
        if (isLocationPermissionGranted()) {
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            initUserLocation(fusedLocationClient);
        }
    }

    private String getApiToken() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);
            return properties.getProperty("API_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initUserLocation(FusedLocationProviderClient fusedLocationClient) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            return;
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                try {
                    updateUserLocation(new Point(latitude, longitude));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void updateUserLocation(Point point) throws ExecutionException, InterruptedException {
        position = new CameraPosition(point, 17, 0, 0);
        updateCity();
        map = mapView.getMapWindow().getMap();
        map.setRotateGesturesEnabled(false);
        map.setNightModeEnabled(isNightMode);
        map.move(position, new Animation(Animation.Type.SMOOTH, 0.5f), null);
        locationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
        locationLayer.setVisible(true);
        locationLayer.setHeadingEnabled(true);
        objCollection = map.getMapObjects();
        if (incidentCreated()) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmaps.get(0), 512, 512, true);
            Bitmap iconBitmap = getIconBitmap(scaledBitmap);
            Bitmap bitmap = getRoundedCornerBitmap(iconBitmap);
            generateIncident(bitmap);
        }
        watchIncident();
        tapOnMap();
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void tapOnMap() {
        InputListener listener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                if (bottomSheetBehavior != null) {
                    bottomSheetBehavior.setPeekHeight(0, true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                if (bottomSheetBehavior != null) {
                    bottomSheetBehavior.setPeekHeight(0, true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        map.addInputListener(listener);
        mapListener.add(listener);
    }

    private void updateCity() {
        Point p = position.getTarget();
        try {
            Geocoder geocoder = new Geocoder(this, new Locale("RU"));
            List<Address> list = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
            String locality = list.get(0).getLocality();
            if (locality != null) {
                city.setText(locality);
                city.setWidth((int) (locality.length() * metrics.widthPixels * 0.035));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateIncident(Bitmap bitmap) throws ExecutionException, InterruptedException {
        IncidentMap incident = new IncidentMap(user, text, position, bitmap, this);
        AddIncidentRequestTask task = new AddIncidentRequestTask(incident, bitmaps, this);
        task.execute();
        Call<Integer> call = task.get();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                Integer t = response.body();
                if (t != null && t.equals(200)) {
                    addIncidentToMap(incident);
                    return;
                }
                onFailure(call, new Throwable(SERVER.NOT_ACCESS.toString()));
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
            }
        });
    }

    private void addIncidentToMap(IncidentMap incident) {
        PlacemarkMapObject mapObject = objCollection.addPlacemark(object -> {
            object.setUserData(incident);
            object.setGeometry(incident.getPoint());
            object.setIcon(ImageProvider.fromBitmap(incident.getImage()), getIconStyle());
        });
        disappear(mapObject);
    }

    private ViewPagerBitmapAdapter getAdapter(int id) {
        return new ViewPagerBitmapAdapter(getApplicationContext(), hashMap.get(id));
    }

    private ViewPagerBitmapAdapter getAdapter() {
        return new ViewPagerBitmapAdapter(getApplicationContext(), this.bitmaps);
    }

    private BottomSheetBehavior.BottomSheetCallback replaceAlpha() {
        BottomSheetBehavior.BottomSheetCallback callback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 1) {
                    buttonFindMe.setVisibility(View.INVISIBLE);
                    buttonChangeTime.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    photo.setVisibility(View.VISIBLE);
                } else if (slideOffset < 1) {
                    buttonFindMe.setVisibility(View.VISIBLE);
                    buttonChangeTime.setVisibility(View.VISIBLE);
                }
                viewPager.setAlpha(slideOffset);
                photo.setAlpha(slideOffset);
                buttonFindMe.setAlpha((float) 0.6 - slideOffset);
                buttonChangeTime.setAlpha((float) 0.6 - slideOffset);
            }
        };
        return callback;
    }

    @SuppressLint("SetTextI18n")
    private void watchIncident() {
        MapObjectTapListener listener = (mapObject, point) -> {
            IncidentMap data = (IncidentMap) mapObject.getUserData();
            if (data != null) {
                String lastUpdateMessage = "Последнее обновление: ";
                Point incidentPoint = data.getPoint();
                double kilometers = Utils.calculateDistanceBetweenPoints(position.getTarget(), incidentPoint);
                lastUpdate.setText(lastUpdateMessage + data.getEventTime());
                description.setText(data.getDescription());
                streetAndKm.setText(data.getAddress() + " - " + kilometers + " км.");
                imageIncident.setImageBitmap(data.getImage());
                viewPager.setAdapter(data.getId() == 0 ? getAdapter() : getAdapter(data.getId()));
                BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(layout);
                bottomSheetBehavior.setPeekHeight((int) (metrics.heightPixels * 0.27), true);
            }
            return true;
        };
        objectListener.add(listener);
        objCollection.addTapListener(listener);
    }

    private void disappear(MapObject mapObject) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> objCollection.remove(mapObject), 60 * 60 * 1000);
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
        int size = 20;
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, width, height, paint);
        canvas.drawRect(size, 0, width - size, height, paint);
        canvas.drawRect(0, size, width, height - size, paint);
        canvas.drawRect(0, height - size, width, height, paint);
        canvas.drawBitmap(bitmap, size, size, null);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(size, size, width - size, height - size, paint);
        return outputBitmap;
    }

    private IconStyle getIconStyle() {
        IconStyle style = new IconStyle();
        style.setRotationType(RotationType.NO_ROTATION);
        style.setScale(metrics.density * 0.12f);
        style.setZIndex(0f);
        return style;
    }

    private void initComponents() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        buttonCrossAdd = findViewById(R.id.addCross);
        buttonFindMe = findViewById(R.id.findme);
        buttonChangeTime = findViewById(R.id.changeTIme);
        lastUpdate = findViewById(R.id.lastUpdate);
        description = findViewById(R.id.description);
        streetAndKm = findViewById(R.id.streetAndKm);
        imageIncident = findViewById(R.id.imageIncident);
        layout = findViewById(R.id.frame_layout_r);
        city = findViewById(R.id.city);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        viewPager = findViewById(R.id.viewPagerHome);
        mapView = findViewById(R.id.mapview);
        photo = findViewById(R.id.photo);
        bottomSheetBehavior = BottomSheetBehavior.from(layout);
        bottomSheetBehavior.setPeekHeight(0);
        layout.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(replaceAlpha());
    }

    private void initPreferences() {
        SharedPreferences preferences = getSharedPreferences("NightModePrefs", Context.MODE_PRIVATE);
        isNightMode = preferences.getBoolean("nightMode", false);
        city.setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
        city.getBackground().setColorFilter(isNightMode ?
                Color.BLACK : getResources().getColor(R.color.beige), PorterDuff.Mode.ADD);
    }

    private void initListeners() {
        buttonCrossAdd.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout_home, new IncidentFragment()).commit());
        buttonFindMe.setOnClickListener(v -> {
            if (position != null) {
                map.move(position, new Animation(Animation.Type.SMOOTH, 0.5f), null);
            }
        });
        buttonChangeTime.setOnClickListener(v -> {
            isNightMode = !isNightMode;
            map.setNightModeEnabled(isNightMode);
            SharedPreferences sharedPreferences = getSharedPreferences("NightModePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("nightMode", isNightMode);
            editor.apply();
            city.setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
            city.getBackground().setColorFilter(isNightMode ?
                    Color.BLACK : getResources().getColor(R.color.beige), PorterDuff.Mode.ADD);
        });
    }

    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                            Manifest.permission.INTERNET
                    }, 122);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home, new HomeFragment()).commit();
                return true;
            }
            case R.id.profile -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home, new ProfileFragment()).commit();
                return true;
            }
            case R.id.news -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home, new NewsFragment()).commit();
                return true;
            }
            case R.id.alarm -> {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home, new AlarmFragment()).commit();
                return true;
            }
        }
        return false;
    }

    private boolean incidentCreated() {
        Intent intent = getIntent();
        ArrayList<Uri> uri = intent.getParcelableArrayListExtra("images");
        String text = intent.getStringExtra("text");
        boolean notification = intent.getBooleanExtra("notification", false);
        if (uri != null && text != null) {
            bitmaps = new ArrayList<>();
            for (Uri u : uri) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), u);
                    bitmaps.add(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.text = text;
            return true;
        }
        return false;
    }

    private boolean notificationCreated() {
        Intent intent = getIntent();
        String uri = intent.getStringExtra("image");
        return intent.getBooleanExtra("notification", false);
    }

    private void initIncidents() {
        SharedPreferences preferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long phone = preferences.getLong("phone", 0);
        String login = preferences.getString("login", null);
        String password = preferences.getString("password", null);
        String token = preferences.getString("token", null);
        user = new User(phone, login, password, token);
        try {
            GetIncidentRequestTask task = new GetIncidentRequestTask();
            task.execute();
            Call<List<Incident>> call = task.get();
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NotNull Call<List<Incident>> call, @NotNull Response<List<Incident>> response) {
                    List<Incident> incidents = response.body();
                    try {
                        if (incidents != null)
                            initMaterials(incidents);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<Incident>> call, @NotNull Throwable t) {
                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initMaterials(List<Incident> incidents) throws ExecutionException, InterruptedException {
        hashMap = new HashMap<>();
        GetIncidentMaterialRequestTask task = new GetIncidentMaterialRequestTask();
        task.execute();
        Call<ResponseBody> call = task.get();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                ResponseBody rb = response.body();
                assert rb != null;
                InputStream is = rb.byteStream();
                /* Чтение архива с материалами */
                try {
                    ZipInputStream zis = (is instanceof ZipInputStream) ? (ZipInputStream) is
                            : new ZipInputStream(new BufferedInputStream(is));
                    ZipEntry ze;
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    while ((ze = zis.getNextEntry()) != null) {
                        String entryName = ze.getName();
                        String folder = entryName.split("/")[0];
                        Integer folderId = Integer.valueOf(folder);
                        ArrayList<Bitmap> bitmaps = hashMap.get(folderId);
                        if (bitmaps == null)
                            bitmaps = new ArrayList<>();
                        Bitmap preview = BitmapFactory.decodeStream(zis, null, opts);
                        bitmaps.add(preview);
                        hashMap.put(folderId, bitmaps);
                    }
                    zis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                /* Отображение на экране */
                map = mapView.getMapWindow().getMap();
                objCollection = map.getMapObjects();
                for (Incident incident : incidents) {
                    int id = incident.getId();
                    List<Bitmap> list = hashMap.get(id);
                    if (list == null)
                        return;
                    Point point = new Point(incident.getLatitude(), incident.getLongitude());
                    /* Bitmap */
                    Bitmap preview = list.get(0);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(preview, 512, 512, true);
                    Bitmap iconBitmap = getIconBitmap(scaledBitmap);
                    Bitmap bitmap = getRoundedCornerBitmap(iconBitmap);
                    IncidentMap incidentMap = new IncidentMap();
                    incidentMap.setIdIncident(incident.getId());
                    incidentMap.setUser(incident.getUser());
                    incidentMap.setEventTime(incident.getTime());
                    incidentMap.setEventDescription(incident.getEventDescription());
                    incidentMap.setLatitude(point.getLatitude());
                    incidentMap.setLongitude(point.getLongitude());
                    incidentMap.setBitmap(bitmap);
                    incidentMap.setActivity(HomeActivity.this);
                    addIncidentToMap(incidentMap);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initMetrix() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }
}