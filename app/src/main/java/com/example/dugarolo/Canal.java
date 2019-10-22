package com.example.dugarolo;

import org.osmdroid.util.GeoPoint;

public class Canal {

    private GeoPoint start;
    private GeoPoint end;

    public Canal(GeoPoint start, GeoPoint end) {
        this.start = start;
        this.end = end;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public void setEnd(GeoPoint end) {
        this.end = end;
    }

    public GeoPoint getStart() {
        return start;
    }

    public void setStart(GeoPoint start) {
        this.start = start;
    }
}
