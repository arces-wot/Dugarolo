package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

public class Field implements Parcelable {

    private String farmName;
    private String id;
    private ArrayList<GeoPoint> area;


    public Field(String farmName, String id, ArrayList<GeoPoint> area) {
        this.farmName = farmName;
        this.id = id;
        this.area = area;
    }

    protected Field(Parcel in) {
        farmName = in.readString();
        id = in.readString();
        area = in.createTypedArrayList(GeoPoint.CREATOR);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    public String getFarmName() {
        return farmName;
    }

    public ArrayList<GeoPoint> getArea() {
        return area;
    }

    public String getId() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(farmName);
        parcel.writeString(id);
        parcel.writeTypedList(area);
    }
}

