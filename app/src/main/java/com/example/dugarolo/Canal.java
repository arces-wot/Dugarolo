package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Canal {

    private String weirId;
    private GeoPoint start;
    private GeoPoint end;

    public Canal(String weirId, GeoPoint start, GeoPoint end) {
        this.weirId = weirId;
        this.start = start;
        this.end = end;
    }

    public String getWeirId() {
        return weirId;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public GeoPoint getStart() {
        return start;
    }

}
