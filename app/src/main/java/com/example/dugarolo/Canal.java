package com.example.dugarolo;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

public class Canal implements Parcelable {

    private String id;
    private GeoPoint start;
    private GeoPoint end;
    private Integer waterLevel;

    public Canal(String id, GeoPoint start, GeoPoint end, Integer waterLevel) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.waterLevel = waterLevel;
    }

    protected Canal(Parcel in) {
        id = in.readString();
        start = in.readParcelable(GeoPoint.class.getClassLoader());
        end = in.readParcelable(GeoPoint.class.getClassLoader());
        if (in.readByte() == 0) {
            waterLevel = null;
        } else {
            waterLevel = in.readInt();
        }
    }

    public static final Creator<Canal> CREATOR = new Creator<Canal>() {
        @Override
        public Canal createFromParcel(Parcel in) {
            return new Canal(in);
        }

        @Override
        public Canal[] newArray(int size) {
            return new Canal[size];
        }
    };

    public String getId() {
        return id;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public GeoPoint getStart() {
        return start;
    }


    public Integer getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(start, flags);
        dest.writeParcelable(end, flags);
        if (waterLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(waterLevel);
        }
    }
}
