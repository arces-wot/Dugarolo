package com.example.dugarolo;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.UUID;

public class Request implements Comparable<Request>{

    private int basicIconId;
    private String name;
    private Integer statusIconId;
    private LocalDate date;

    public Request(Integer basicIconId, String name, LocalDate localDate, Integer statusIconId) {
        this.basicIconId = basicIconId;
        this.name = name;
        this.statusIconId = statusIconId;
        this.date = localDate;
    }

    public String getName() {
        return name;
    }

    public int getBasicIconId() {
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


    public String toString(){
        return this.name;
    }

    @Override
    public int compareTo(Request o) {
       return this.date.toDate().compareTo(o.getDate().toDate());
    }
}
