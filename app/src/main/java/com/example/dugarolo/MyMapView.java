package com.example.dugarolo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;


import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressLint("ParcelCreator")
public class MyMapView extends MapView implements Parcelable {

    Globals sharedData = Globals.getInstance();

    //da inserire più colori, tanti quante sono le aziende operative
    int[] FarmColor = {
            (getResources().getColor(R.color.colorCompany1)),
            (getResources().getColor(R.color.colorCompany2)),
            (getResources().getColor(R.color.colorCompany3)),
            (getResources().getColor(R.color.colorCompany4))
            //(getResources().getColor(R.color.colorPrimaryDark))
    };

    FarmColor farmColor = new FarmColor("", 0);
    ArrayList<FarmColor> farmColorsList = new ArrayList<FarmColor>();

    Random random = new Random();
    int min = 0, max = FarmColor.length - 1, c = ((max - min) + 1);

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void drawWeirs(final ArrayList<Weir> weirs, ArrayList<Marker> weirMarkers) {
        for (Weir weir : weirs) {
            //Toast.makeText(getContext(), "Drawing weir #" + weir.getId(), Toast.LENGTH_LONG).show();
            Marker marker = new Marker(this);
            marker.setPosition(weir.getPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Drawable weirIcon = getResources().getDrawable(R.drawable.weir);
            Bitmap bitmap = ((BitmapDrawable) weirIcon).getBitmap();
            Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
            marker.setIcon(resizedWeirIcon);
            marker.setInfoWindow(null);
            marker.setId(weir.getId());
            weirMarkers.add(marker);
            this.getOverlays().add(marker);
            this.invalidate();
        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmap(Context context, int drawableId) {
        //Drawable farmer=getResources().getDrawable(drawableId);
        //farmer.setTint(R.color.colorCompany2);


        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    /*public void drawIcon(ArrayList<Farm> farms, ArrayList<Marker> farmerMarkers, int size) {
        ArrayList<GeoPoint> iconsPosition = new ArrayList<>();


        for (Farm farm : farms) {
            Marker marker = new Marker(this);
            iconsPosition = farm.getIconPosition();
            for (int i = 0; i <= iconsPosition.size(); i++) {

                marker.setPosition(iconsPosition.get(i));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(getBitmap(getContext(), R.drawable.ic_farmercolor_no_backgroud), size, size, true));
                marker.setIcon(resizedWeirIcon);
                marker.setInfoWindow(null);
                marker.setId(farm.getName());
                farmerMarkers.add(marker);
                this.getOverlays().add(marker);
                this.invalidate();
            }
        }
    }*/
    public void drawIcon(ArrayList<Farm> farms, ArrayList<Marker> farmerMarkers, int size) {

        for (Farm farm : farms) {
            Marker marker = new Marker(this);
            marker.setPosition(farm.getIconPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(getBitmap(getContext(), R.drawable.ic_farmercolor_no_backgroud), size, size, true));
            marker.setIcon(resizedWeirIcon);
            marker.setInfoWindow(null);
            marker.setId(farm.getName());
            farmerMarkers.add(marker);
            this.getOverlays().add(marker);
            this.invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Marker drawPosition(GeoPoint position) {
        Marker newMarker = new Marker(this);
        newMarker.setPosition(position);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(getBitmap(getContext(), R.drawable.ic_map_marker), 70, 70, true));
        newMarker.setIcon(resizedWeirIcon);
        newMarker.setInfoWindow(null);
        newMarker.setId("Dugarolo position");
        this.getOverlays().add(newMarker);
        this.invalidate();
        return newMarker;

    }


    public GeoPoint midPoint(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        return GeoPoint.fromCenterBetween(geoPoint1, geoPoint2);
    }

    //everything starts here
    public void drawFarms(ArrayList<Farm> farms,ArrayList<Request> requests) {

        for (Farm farm : farms) {
            ArrayList<Field> farmFields = farm.getFields();
            for (Field field : farmFields) {
                this.drawField(field,requests);
            }
        }
    }

    //here I set the area where the field is in the map
    public void drawField(Field field,ArrayList<Request> requests) {

        ArrayList<GeoPoint> fieldArea = field.getArea();
        Polygon polygon = new Polygon();

        for (GeoPoint point : fieldArea) {
            polygon.addPoint(point);
        }

        fillFarms(field, polygon,requests);
    }

    //this method is used to fill with color the polygon I just draw
    public void fillFarms(Field field, Polygon polygon,ArrayList<Request> requests) {

        /*int randomColor, finalColor;

        //if the arrayList I use to assign color to owner is empty it means that it's the first time
        //the program get in this method
        if (sharedData.getFarmColors() != null) {
            farmColorsList = sharedData.getFarmColors();
        }

        //Log.d("nomeFarm", field.getFarmName());

        if (sharedData.searchForAssignedFarm(field.getFarmName()) != 0) {
            finalColor = checkColorIfExist(field.getFarmName(), 0);
            manipulateColor(finalColor, 0.8f);
            polygon.getOutlinePaint().setColor(finalColor);
            polygon.setFillColor(finalColor);
        } else {
            randomColor = getRandomColor(field.getFarmName());
            finalColor = checkColorIfExist(field.getFarmName(), randomColor);
            manipulateColor(finalColor, 0.8f);
            polygon.setFillColor(finalColor);
        }*/
        int color=getResources().getColor(R.color.colorOtherStatus);
        for (Request r:requests)
            if (r.getField().equals(field)){
                color=getColorByStatus(r);
                break;
            }
        //manipulateColor(color, 0.8f);
        //polygon.getOutlinePaint().setColor(color);
        polygon.setFillColor(color);

        int strokeColor = manipulateColor(color, 0.8f);

        polygon.getOutlinePaint().setStrokeWidth(5);
        polygon.getOutlinePaint().setColor(strokeColor);
        this.getOverlayManager().add(polygon);
        this.invalidate();
    }

    private int getColorByStatus(Request r) {
        if (r.getStatus().equalsIgnoreCase("Accepted"))
            return getResources().getColor(R.color.acceptedColor);
        else if(r.getStatus().equalsIgnoreCase("Scheduled"))
            return getResources().getColor(R.color.scheduledColor);
        else if(r.getStatus().equalsIgnoreCase("Ongoing"))
            return getResources().getColor(R.color.ongoingColor);
        else if(r.getStatus().equalsIgnoreCase("Interrupted"))
            return getResources().getColor(R.color.interruptedColor);
        else
            return getResources().getColor(R.color.colorOtherStatus);

    }

    public int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }


    //this method is used to assign a random color from the array made by color
    //to variable 'color' which return
    public int getRandomColor(String nameFarm) {

        int randomElement = 0, color = 0;

        randomElement = random.nextInt(c) + min;
        color = FarmColor[randomElement];

        return color;
    }


    //this method does different kind of control in order to dodge double color assignment
    public int checkColorIfExist(String nameFarm, int randomElement) {

        int check = 0;


        if (farmColorsList.size() != 0 || farmColorsList != null) {

            //if the arraylist isn't empty, atleast one company has a color
            for (int i = 0; i < farmColorsList.size(); i++) {
                if (nameFarm.equals(farmColorsList.get(i).getNameFarm())) {
                    //Check if this company has already a color, if it has, I return his own color
                    return farmColorsList.get(i).getIdColor();
                }
            }

            //In order to check if the color is already used, I use a variable check
            /*for (int i = 0; i < farmColorsList.size(); i++) {
                    if (randomElement == farmColorsList.get(i).getIdColor()) {
                        check = 1;
                    }
            }*/

            //if color is not used
            if (check == 0) {
                //I instance an object of FarmColor type
                FarmColor farmColor = new FarmColor(nameFarm, randomElement);
                //adding in the local list
                farmColorsList.add(farmColor);
                //adding in the global list
                sharedData.updateList(farmColor);

                //return the color
                return randomElement;
            } else {
                //If already used
                //Recalling the random color method
                randomElement = getRandomColor(nameFarm);
                //checking again if the color is already use
                checkColorIfExist(nameFarm, randomElement);
            }

        } else {
            //if the arraylist is empty it means that I have to create the first element with any color,
            //that's why I didn't put any checks in the color used
            FarmColor farmColor = new FarmColor(nameFarm, randomElement);
            farmColorsList.add(farmColor);
            sharedData.updateList(farmColor);

            return randomElement;
        }
        return 0;
    }

    public void drawCanals(ArrayList<Canal> canals) {
        for (Canal canal : canals) {
            Polyline line = new Polyline();
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(canal.getStart());
            geoPoints.add(canal.getEnd());
            line.setPoints(geoPoints);
            line.getOutlinePaint().setColor(Color.parseColor("#1abc9c"));
            this.getOverlayManager().add(line);
            /*
            line.setOnClickListener(new Polyline.OnClickListener() {
                @Override
                public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                    double lat = eventPos.getLatitude();
                    double lon = eventPos.getLongitude();
                    //Toast.makeText(getContext(), "Lat: " + lat + ", " + "Lon: " + lon, Toast.LENGTH_LONG).show();
                    GeoPoint pos = new GeoPoint(lat, lon);
                    Marker marker = new Marker(mapView);
                    marker.setPosition(pos);
                    marker.setTitle("Lat: " + lat + ", " + "Lon: " + lon);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    mapView.getOverlayManager().add(marker);
                    mapView.invalidate();
                    return false;
                }
            });
             */
        }
    }

    public void drawTextMarkers(ArrayList<Marker> textMarkers) {
        for (Marker textMarker : textMarkers) {
            this.getOverlayManager().add(textMarker);
            this.invalidate();
        }
    }

    public void toStringFarmColor() {
        for (int i = 0; i < farmColorsList.size(); i++) {
            Log.d("FarmColorToString", farmColorsList.get(i).toString());
        }
    }

    public int getFarmColorSize() {
        return FarmColor.length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(FarmColor);
        parcel.writeInt(min);
        parcel.writeInt(max);
        parcel.writeInt(c);
    }

    public void clear(){
        this.getOverlayManager().clear();
    }
}
