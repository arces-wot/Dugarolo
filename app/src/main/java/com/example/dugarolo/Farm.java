package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

public class Farm implements Parcelable {

    private String name;
    private ArrayList<Field> fields = new ArrayList<>();

    public Farm(String name, ArrayList<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    public ArrayList<Field> getFields() {
        return fields;
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
