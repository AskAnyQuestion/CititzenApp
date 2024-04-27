package com.example.application.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import com.example.application.model.Incident;
import com.example.application.model.User;
import com.yandex.mapkit.geometry.Point;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

public class IncidentMap extends Incident {
    private Activity activity;
    private Bitmap bitmap;
    private LocalDateTime eventTime;

    public IncidentMap() {}

    public IncidentMap(String eventDescription, Point point, Bitmap bitmap, Activity activity) {
        super(new User(), eventDescription, point);
        this.activity = activity;
        this.bitmap = bitmap;
        this.eventTime = LocalDateTime.now();
    }

    public void setDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getDescription() {
        return eventDescription;
    }

    @SuppressLint("DefaultLocale")
    public CharSequence getEventTime() {
        return String.format("%02d:%02d", eventTime.getHour(), eventTime.getMinute());
    }

    @SuppressLint("DefaultLocale")
    public CharSequence getDateTime() {
        return eventTime.getDayOfMonth() + " апреля " + String.format("%02d:%02d", eventTime.getHour(), eventTime.getMinute());
    }

    public String getAddress() {
        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), new Locale("RU"));
        Address address;
        try {
            address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return address.getThoroughfare();
    }

    public Point getPoint() {
        return new Point(latitude, longitude);
    }

    public Bitmap getImage() {
        return bitmap;
    }

    public Incident toIncident() {
        return new Incident(new User(), this.eventDescription, getPoint());
    }
}
