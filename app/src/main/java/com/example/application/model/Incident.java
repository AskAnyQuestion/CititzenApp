package com.example.application.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import com.yandex.mapkit.geometry.Point;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class Incident {
    private String description;
    private LocalTime time;
    private LocalDateTime dateTime;
    private List<Address> addressList;
    private Point point;
    private Bitmap bitmap;

    public Incident() {
        this.dateTime = LocalDateTime.now();
    }

    public Incident(Point point, Bitmap bitmap, String description, Activity activity) {
        super();
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

    @SuppressLint("DefaultLocale")
    public CharSequence getDateTime() {
        return dateTime.getDayOfMonth() + " апреля " + String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
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

    public void setDescription(String description) {
        this.description = description;
    }
}
