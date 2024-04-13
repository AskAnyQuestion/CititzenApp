package com.example.application.data;

import android.app.Activity;
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
    private final Point p;

    public IncidentData(Point p, String description, Activity activity) {
        this.time = LocalTime.now();
        this.description = description;
        this.p = p;
        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), new Locale("RU"));
        try {
            this.addressList = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
