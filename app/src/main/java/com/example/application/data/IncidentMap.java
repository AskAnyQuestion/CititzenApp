package com.example.application.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import com.example.application.model.Incident;
import com.example.application.model.User;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import java.io.IOException;
import java.util.Locale;

public class IncidentMap extends Incident {
    private Activity activity;
    private Bitmap bitmap;

    public IncidentMap() {
    }

    public IncidentMap(Integer id, User user, String eventDescription, Point point, Bitmap bitmap, Activity activity) {
        super(id, user, eventDescription, point);
        this.activity = activity;
        this.bitmap = bitmap;
    }

    public IncidentMap(User user, String eventDescription, CameraPosition position, Bitmap bitmap, Activity activity) {
        super(user, eventDescription, position.getTarget());
        this.activity = activity;
        this.bitmap = bitmap;
    }

    public void setDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getDescription() {
        return eventDescription;
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
        return new Incident(getUser(), getEventDescription(), getPoint());
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
