package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public class Weir implements Parcelable {
    private String id;
    private Integer maxLevel;
    private Integer minLevel;
    private Integer currentOpenLevel;
    private GeoPoint position;

    public Weir(String id, Integer maxLevel, Integer minLevel, Integer openLevel, GeoPoint position) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
        this.currentOpenLevel = openLevel;
        this.position = position;
    }

    protected Weir(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            maxLevel = null;
        } else {
            maxLevel = in.readInt();
        }
        if (in.readByte() == 0) {
            minLevel = null;
        } else {
            minLevel = in.readInt();
        }
        if (in.readByte() == 0) {
            currentOpenLevel = null;
        } else {
            currentOpenLevel = in.readInt();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMaxLevel() {
        return this.maxLevel;
    }

    public Integer getMinLevel() {
        return this.minLevel;
    }

    public Integer getOpenLevel() {
        return currentOpenLevel;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setOpenLevel(Integer openLevel) {
        this.currentOpenLevel = openLevel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (maxLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxLevel);
        }
        if (minLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(minLevel);
        }
        if (currentOpenLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(currentOpenLevel);
        }
        dest.writeParcelable(position, flags);
    }
}
