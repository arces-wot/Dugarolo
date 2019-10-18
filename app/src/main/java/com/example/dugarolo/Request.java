package com.example.dugarolo;

import java.util.ArrayList;

public class Request {
    private Integer basicIconId;
    private String name;
    private Integer statusIconId;


    public static final Request[] requests = {
            new Request(R.drawable.request_cancelled, "Bertacchini\'s farm", R.drawable.request_interrupted),
            new Request(R.drawable.request_completed, "Ferrari\'s farm", R.drawable.status_unknown)
    };

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

    public void setStatusIconId(Integer statusIconId) {
        this.statusIconId = statusIconId;
    }

    /*
    public void populateArray() {
        Request request1 = new Request(R.drawable.request_cancelled, "Bertacchini\'s farm", R.drawable.request_interrupted);
        Request request2 = new Request(R.drawable.request_completed, "Ferrari\'s farm", R.drawable.status_unknown);
        requests.add(request1);
        requests.add(request2);
    }
    */


    public String toString(){
        return this.name;
    }
}
