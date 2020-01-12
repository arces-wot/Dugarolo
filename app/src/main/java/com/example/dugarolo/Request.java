package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

public class Request implements Comparable<Request>, Parcelable {

    private String id;
    private String name;
    private String status;
    private DateTime dateTime;
    private String waterVolume;
    private Field field;

    public Request(String id, String name, DateTime dateTime, String status, String waterVolume, Field field) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.dateTime = dateTime;
        this.waterVolume = waterVolume;
        this.field = field;
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

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String toString(){
        return this.name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Request o) {
        return this.dateTime.toDateTime().compareTo(o.getDateTime().toDateTime());
    }

    protected Request(Parcel in) {
        id = in.readString();
        name = in.readString();
        status = in.readString();
        dateTime = (DateTime) in.readValue(DateTime.class.getClassLoader());
        waterVolume = in.readString();
        field = (Field) in.readValue(Field.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeValue(dateTime);
        dest.writeString(waterVolume);
        dest.writeValue(field);
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
