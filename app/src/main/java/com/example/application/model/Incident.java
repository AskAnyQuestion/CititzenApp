package com.example.application.model;

import android.annotation.SuppressLint;
import com.example.application.map.IncidentMap;
import com.yandex.mapkit.geometry.Point;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;


public class Incident {
    protected Integer idIncident;
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

    public Integer getId() {
        return idIncident;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @SuppressLint("DefaultLocale")
    public CharSequence getEventTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
        String formattedTime = null;
        try {
            Date parsedDate = sdf.parse(eventTime.toString());
            assert parsedDate != null;
            int hours = parsedDate.getHours();
            int minutes = parsedDate.getMinutes();
            formattedTime = String.format("%02d:%02d", hours, minutes);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }
}
