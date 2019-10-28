package com.example.dugarolo;

import org.osmdroid.views.overlay.Polygon;

public class Farm {

    private String name;
    private Polygon area;

    public Farm(String name, Polygon area) {
        this.name = name;
        this.area = area;
    }

    public Polygon getArea() {
        return area;
    }

    public String getName() {
        return name;
    }
}
