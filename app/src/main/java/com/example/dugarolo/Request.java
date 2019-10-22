package com.example.dugarolo;

import org.joda.time.LocalDate;

public class Request implements Comparable<Request>{

    private Integer basicIconId;
    private String name;
    private Integer statusIconId;
    private LocalDate date;


    public static final Request[] requests = {
            new Request(R.drawable.swamp_leaf, "Bertacchini\'s farm", new LocalDate(2019, 9, 18, null), R.drawable.request_interrupted),
            new Request(R.drawable.swamp_leaf, "Ferrari\'s farm", new LocalDate(2019, 9, 19, null), R.drawable.status_unknown)
    };

    public Request(Integer basicIconId, String name, LocalDate localDate, Integer statusIconId) {
        this.basicIconId = basicIconId;
        this.name = name;
        this.statusIconId = statusIconId;
        this.date = localDate;
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

    public LocalDate getDate() {
        return date;
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

    @Override
    public int compareTo(Request o) {
       return this.date.toDate().compareTo(o.getDate().toDate());
    }
}
