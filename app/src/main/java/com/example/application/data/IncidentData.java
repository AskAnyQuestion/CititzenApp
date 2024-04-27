package com.example.application.data;

import com.example.application.model.Incident;
import okhttp3.MultipartBody;

import java.util.ArrayList;

public class IncidentData {
    private Incident incident;
    private ArrayList <MultipartBody.Part> list;

    public IncidentData(Incident incident, ArrayList <MultipartBody.Part> list) {
        this.incident = incident;
        this.list = list;
    }

    public void setList(ArrayList<MultipartBody.Part> list) {
        this.list = list;
    }

    public ArrayList<MultipartBody.Part> getList() {
        return list;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Incident getIncident() {
        return incident;
    }

}
