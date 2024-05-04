package com.example.application.model;

public class Notification {
    private Incident incident;
    private User user;

    public Notification(Incident incident, User user) {
        this.incident = incident;
        this.user = user;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
