package com.example.application.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import com.yandex.mapkit.geometry.Point;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class IncidentData {
    private final String description;
    private final LocalTime time;
    private final List<Address> addressList;
    private final Point point;
    private final Bitmap bitmap;

    public IncidentData(Point point, Bitmap bitmap, String description, Activity activity) {
        this.time = LocalTime.now();
        this.description = description.substring(0, 1).toUpperCase() + description.substring(1);
        this.point = point;
        this.bitmap = bitmap;
        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), new Locale("RU"));
        try {
            this.addressList = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return description;
    }

    @SuppressLint("DefaultLocale")
    public CharSequence getTime() {
        return String.format("%02d:%02d", time.getHour(), time.getMinute());
    }

    public String getAddress() {
        return addressList.get(0).getThoroughfare();
    }

    public Point getPoint() {
        return point;
    }

    public Bitmap getImage() {
        return bitmap;
    }
}
