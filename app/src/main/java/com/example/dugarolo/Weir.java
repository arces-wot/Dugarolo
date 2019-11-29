package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public class Weir implements Parcelable {
    private String number;
    private String farm;
    private Integer openLevel;
    private GeoPoint position;

    public Weir(String number, String farm, Integer openLevel, GeoPoint position) {
        this.farm = farm;
        this.number = number;
        this.openLevel = openLevel;
        this.position = position;
    }

    protected Weir(Parcel in) {
        number = in.readString();
        farm = in.readString();
        if (in.readByte() == 0) {
            openLevel = null;
        } else {
            openLevel = in.readInt();
        }
        position = in.readParcelable(GeoPoint.class.getClassLoader());
    }

    public static final Creator<Weir> CREATOR = new Creator<Weir>() {
        @Override
        public Weir createFromParcel(Parcel in) {
            return new Weir(in);
        }

        @Override
        public Weir[] newArray(int size) {
            return new Weir[size];
        }
    };

    public String getFarm() {
        return farm;
    }

    public String getNumber() {
        return number;
    }

    public Integer getOpenLevel() {
        return openLevel;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setOpenLevel(Integer openLevel) {
        this.openLevel = openLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(farm);
        if (openLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(openLevel);
        }
        dest.writeParcelable(position, flags);
    }
}
