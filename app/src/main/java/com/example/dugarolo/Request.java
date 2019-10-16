package com.example.dugarolo;

import java.util.ArrayList;

public class Request {
    private Integer basicIconId;
    private String name;
    private Integer statusIconId;


    public static final ArrayList<Request> requests = new ArrayList<>();

    public Request(Integer basicIconId, String name, Integer statusIconId) {
        this.basicIconId = basicIconId;
        this.name = name;
        this.statusIconId = statusIconId;
    }

    public String getName() {
        return name;
    }

    public Integer getBasicIconId() {
        return basicIconId;
    }

    public Integer getStatusIconId() {
        return statusIconId;
    }

    public String toString(){
        return this.name;
    }
}
