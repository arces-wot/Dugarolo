package com.example.dugarolo;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

public class Farm implements Parcelable {

    private String name;
    private ArrayList<Field> fields = new ArrayList<>();
    private GeoPoint iconPosition;
   // public static int[] icons = new int[]{R.drawable.farmericon1, R.drawable.farmer_icon2};
    //public static int[] colors = new int[]{R.color.colorCompany1, R.color.colorCompany2};
   // private static int i=0;
    private int icon;
    private int color;




    public Farm(String name, ArrayList<Field> fields) {
        //i++;
        this.name = name;
        this.fields = fields;
        this.iconPosition=iconPosition();
        //this.icon=icons[i];
        //this.color=colors[i];
        }

    public ArrayList<Field> getFields() {
        return fields;
    }

    protected Farm(Parcel in) {
        name = in.readString();
    }
    public int getIcon(){return icon;}
    public int getColor(){return color;}

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
    }


    public GeoPoint iconPosition() {
        int i=0;
        double lat=0;
        double lon=0;
        for (Field f : fields)
            for (GeoPoint g : f.getArea()) {
                i=i+1;
                lat=g.getLatitude()+lat;
                lon=g.getLongitude()+lon;
            }
        return new GeoPoint(lat/i,lon/i);

    }
}
