package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Canal {

    private String id;
    private GeoPoint start;
    private GeoPoint end;
    private Integer waterLevel;

    public Canal(String id, GeoPoint start, GeoPoint end, Integer waterLevel) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waterLevel = waterLevel;
    }

    public String getId() {
        return id;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public GeoPoint getStart() {
        return start;
    }


    public Integer getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }
}
