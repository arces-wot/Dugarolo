package com.example.dugarolo;

import org.joda.time.DateTime;
public class Request implements Comparable<Request>{

    private String name;
    private String status;
    private DateTime dateTime;
    private String waterVolume;

    public Request(String name, DateTime dateTime, String status, String waterVolume) {
        this.name = name;
        this.status = status;
        this.dateTime = dateTime;
        this.waterVolume = waterVolume;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWaterVolume() {
        return waterVolume;
    }

    public void setWaterVolume(String waterVolume) {
        this.waterVolume = waterVolume;
    }

    public String toString(){
        return this.name;
    }

    @Override
    public int compareTo(Request o) {
       return this.dateTime.toDateTime().compareTo(o.getDateTime().toDateTime());
    }
}
