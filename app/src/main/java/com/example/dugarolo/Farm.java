package com.example.dugarolo;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Farm implements Parcelable {

    private String name;
    private ArrayList<Field> fields = new ArrayList<>();
    private GeoPoint iconPosition;

    public Farm(String name, ArrayList<Field> fields) {
        this.name = name;
        this.fields = fields;
        //this.iconPosition = iconPosition;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    protected Farm(Parcel in) {
        name = in.readString();
        in.readTypedList(fields, Field.CREATOR);
        iconPosition = (GeoPoint) in.readSerializable();
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


    public GeoPoint getIconPosition() {
        return iconPosition;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(fields);
        dest.writeSerializable(iconPosition);
    }


}

