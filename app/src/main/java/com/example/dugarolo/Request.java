package com.example.dugarolo;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.Date;


public class Request implements Comparable<Request>, Parcelable {

    private String id;
    private String name;
    private String status;
    private Date dateTime;
    private String waterVolume;
    private Field field;
    private String message;
    private String type;
    private String channel;
    private String nameChannel;
    //variabile che mi dice se è in corso o in attesa
    //non la metto nel costruttore perchè se no faccio danni
    private int currentStat;
    private boolean isExpanded;


    public Request(String id, String name, DateTime dateTime, String status, String waterVolume, Field field, String message,
                   String channel, String type, String nameChannel) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.dateTime = dateTime.toDate();
        this.waterVolume = waterVolume;
        this.field = field;
        this.message = message;
        this.channel = channel;
        this.type = type;
        this.isExpanded = false;
        this.nameChannel = nameChannel;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    /*getters variabili da aggiungere
    public String getCurrentStat() {
        return currentStat;
    }*/

    public Boolean getIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(Boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public String getChannel(){return channel;}

    public String getType(){return type; };

    public String getNameChannel() {
        return nameChannel;
    }

    public void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public DateTime getDateTime() {
        return new DateTime(dateTime);
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

    public void setCurrentStat(int status){this.currentStat = status;}

    public String toString(){
        return this.name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int compareTo(Request o) {
        return this.dateTime.compareTo(o.getDateTime().toDate());
    }


    @RequiresApi(api = Build.VERSION_CODES.M)

    protected Request(Parcel in) {
        id = in.readString();
        name = in.readString();
        status = in.readString();
        dateTime = (Date) in.readValue(Date.class.getClassLoader());
        waterVolume = in.readString();
        field = (Field) in.readValue(Field.class.getClassLoader());
        message = in.readString();
        type=in.readString();
        channel=in.readString();
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
        dest.writeString(message);
        dest.writeString(type);
        dest.writeString(channel);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        @RequiresApi(api = Build.VERSION_CODES.M)

        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    static class SortByDate implements Comparator<Request> {
        public int compare(Request a, Request b) {
            return a.compareTo(b);
        }
    }
    static class SortByChannel implements Comparator<Request> {  //per ora ordine alfabetico
        public int compare(Request a, Request b) {
            return a.getChannel().compareToIgnoreCase(b.getChannel());
        }
    }
    static class SortByStatus implements Comparator<Request> {
        public int compare(Request a, Request b) {
            return a.getStatus().compareToIgnoreCase(b.getStatus());
        }
    }
}
