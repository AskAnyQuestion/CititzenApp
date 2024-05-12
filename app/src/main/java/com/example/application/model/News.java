package com.example.application.model;

import java.sql.Timestamp;

public class News {
    private Timestamp eventTime;
    protected Double latitude;
    protected Double longitude;
    private String description;
    private String addition;

    public News() {
    }

    public News(Timestamp eventTime, Double latitude, Double longitude, String description, String addition) {
        this.eventTime = eventTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.addition = addition;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }
}
