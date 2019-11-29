package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.views.overlay.Polygon;

public class Farm implements Parcelable {

    private String name;
    private Polygon area;

    public Farm(String name, Polygon area) {
        this.name = name;
        this.area = area;
    }

    protected Farm(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Farm> CREATOR = new Creator<Farm>() {
        @Override
        public Farm createFromParcel(Parcel in) {
            return new Farm(in);
        }

        @Override
        public Farm[] newArray(int size) {
            return new Farm[size];
        }
    };

    public Polygon getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
