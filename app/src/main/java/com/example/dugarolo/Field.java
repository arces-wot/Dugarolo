package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Field implements Serializable {

    private String farmName;
    private String id;
    private ArrayList<GeoPoint> area;

    public Field(String farmName, String id, ArrayList<GeoPoint> area) {
        this.farmName = farmName;
        this.id = id;
        this.area = area;
    }

    public String getFarmName() {
        return farmName;
    }

    public ArrayList<GeoPoint> getArea() {
        return area;
    }

    public String getId() {
        return id;
    }
}
