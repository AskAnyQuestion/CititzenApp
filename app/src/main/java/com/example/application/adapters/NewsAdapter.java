package com.example.application.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.model.News;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.yandex.mapkit.geometry.Point;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<News> {
    private final Context context;
    private final List<News> news;
    private final HashMap<Integer, Bitmap> hashMap;
    private TextView hourAndKm, description, street, addition;
    private ImageView image;

    public NewsAdapter(Context context, List<News> news, HashMap<Integer, Bitmap> hashMap) {
        super(context, R.layout.item_alarm, news);
        this.context = context;
        this.news = news;
        this.hashMap = hashMap;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder")
        View view = inflater.inflate(R.layout.item_news, parent, false);
        init(view);
        initNews(position);
        return view;
    }

    private void init(View view) {
        hourAndKm = view.findViewById(R.id.hourAndKm);
        description = view.findViewById(R.id.description);
        street = view.findViewById(R.id.street);
        image = view.findViewById(R.id.bitmap);
        addition = view.findViewById(R.id.addition);
    }

    @SuppressLint("SetTextI18n")
    private void initNews(int position) {
        News n = news.get(position);
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(getContext());
        @SuppressLint("MissingPermission")
        Task<Location> task = client.getLastLocation();
        Point point = new com.yandex.mapkit.geometry.Point(n.getLatitude(), n.getLongitude());
        double distance = Utils.getLocation(task, point);
        /* Часы и километры */
        long now = new Date().getTime();
        long time = n.getEventTime().getTime();
        long milliseconds = now - time;
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (hours <= 24)
            hourAndKm.setText(hours + " ч. назад - " + distance + " км.");
        else {
            long days = (hours / 24);
            hourAndKm.setText(days + " д. назад - " + distance + " км.");
        }
        /* Улица */
        String addr = getAddress(point);
        street.setText(addr);
        /* Изображение */
        image.setImageBitmap(hashMap.get(position + 1));
        /* Описание */
        description.setText(n.getDescription());
        /* Дополнение */
        addition.setText(n.getAddition());
    }

    public String getAddress(Point point) {
        Geocoder geocoder = new Geocoder(getContext(), new Locale("RU"));
        Address address;
        try {
            address = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return address.getThoroughfare();
    }
}
