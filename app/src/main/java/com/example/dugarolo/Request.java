package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

public class Request implements Comparable<Request>, Parcelable {

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

    protected Request(Parcel in) {
        name = in.readString();
        status = in.readString();
        dateTime = (DateTime) in.readValue(DateTime.class.getClassLoader());
        waterVolume = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(status);
        dest.writeValue(dateTime);
        dest.writeString(waterVolume);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

}
