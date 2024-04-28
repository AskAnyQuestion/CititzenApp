package com.example.application.model;

import com.yandex.mapkit.geometry.Point;

import java.sql.Timestamp;

import java.util.Date;


public class Incident {
    protected User user;
    protected String eventDescription;
    protected Timestamp eventTime;
    protected Double latitude;
    protected Double longitude;

    public Incident() {}

    public Incident(User user, String eventDescription, Point point) {
        this.eventTime = new Timestamp(new Date().getTime());
        this.eventDescription = eventDescription.substring(0, 1).toUpperCase()
                + eventDescription.substring(1);
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getEventDescription() {
        return eventDescription;
    }
}
