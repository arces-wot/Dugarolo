package com.example.dugarolo;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Farm implements Parcelable {

    private String name;
    private ArrayList<Field> fields = new ArrayList<>();
    //private ArrayList<GeoPoint> iconsPosition=new ArrayList<>();
    private GeoPoint iconPosition;

<<<<<<< HEAD
    public Farm(String name, ArrayList<Field> fields, GeoPoint iconPosition) {
=======



    public Farm(String name, ArrayList<Field> fields,GeoPoint iconPosition) {
>>>>>>> fa72bf77f8c15404669ce4c982beb6a0bba02d88
        this.name = name;
        this.fields = fields;
        //this.iconsPosition=iconsPosition();
        this.iconPosition=iconPosition;
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

   /* public ArrayList<GeoPoint> getIconsPositionList() {
        return iconsPosition;
    }*/
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
    }


    /*public ArrayList<GeoPoint> iconsPosition() {
        ArrayList<GeoPoint> icons=new ArrayList<>();
        int i=0;
        double lat=0;
        double lon=0;
        for (Field f : fields) {
            for (GeoPoint g : f.getArea()) {
                i = i + 1;
                lat = g.getLatitude() + lat;
                lon = g.getLongitude() + lon;
            }
            icons.add(new GeoPoint(lat/i,lon/i));
        }
        return icons;

    }*/
    /*public GeoPoint iconPosition() {
<<<<<<< HEAD

=======
>>>>>>> fa72bf77f8c15404669ce4c982beb6a0bba02d88
        int i=0;
        double lat=0;
        double lon=0;
        for (Field f : fields){
            for (GeoPoint g : f.getArea()) {
                i = i + 1;
                lat = g.getLatitude() + lat;
                lon = g.getLongitude() + lon;
            }
        }

        return new GeoPoint(lat/i,lon/i);

    }*/
}
