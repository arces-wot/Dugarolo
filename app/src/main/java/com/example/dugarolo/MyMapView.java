package com.example.dugarolo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Color.YELLOW;

public class MyMapView extends MapView {

    Globals sharedData = Globals.getInstance();


    //da inserire più colori, tanti quante sono le aziende operative
    int[] FarmColor = {(getResources().getColor(R.color.colorCompany1)),
            (getResources().getColor(R.color.colorCompany2))
            //(getResources().getColor(R.color.colorAccepted)),
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

    public void drawIcon(ArrayList<Farm> farms, ArrayList<Marker> farmerMarkers,int size) {
        Drawable farmerIcon;
        for (Farm farm : farms) {
            Marker marker = new Marker(this);
            marker.setPosition(farm.getIconPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            if (farm.getName().equalsIgnoreCase("Bertacchini's Farm"))
                farmerIcon = getResources().getDrawable(R.drawable.farmericon1);
            else if (farm.getName().equalsIgnoreCase("Ferrari's Farm"))
                farmerIcon = getResources().getDrawable(R.drawable.farmer_icon2);
            else
                farmerIcon = getResources().getDrawable(R.drawable.farmericon1);
            Bitmap bitmap = ((BitmapDrawable) farmerIcon).getBitmap();
            Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
            marker.setIcon(resizedWeirIcon);
            marker.setInfoWindow(null);
            marker.setId(farm.getName());
            farmerMarkers.add(marker);
            this.getOverlays().add(marker);
            this.invalidate();
        }
    }


    public GeoPoint midPoint(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        return GeoPoint.fromCenterBetween(geoPoint1, geoPoint2);
    }

    //everything starts here
    public void drawFarms(ArrayList<Farm> farms) {

        for (Farm farm : farms) {
            ArrayList<Field> farmFields = farm.getFields();
            for (Field field : farmFields) {
                this.drawField(field);
            }
        }
    }

    //here I set the area where the field is in the map
    private void drawField(Field field) {

        ArrayList<GeoPoint> fieldArea = field.getArea();
        Polygon polygon = new Polygon();

        for (GeoPoint point : fieldArea) {
            polygon.addPoint(point);
        }

        fillFarms(field, polygon);
    }

    //this method is used to fill with color the polygon I just draw
    public void fillFarms(Field field, Polygon polygon) {

        //If true it means that this owner has already a color assigned, else, it needs a color
        boolean bertCheck = sharedData.isCheckIfAlreadyAssignedBert();
        boolean ferrCheck = sharedData.isCheckIfAlreadyAssignedFerr();
        int randomColor, finalColor;

        //if the arrayList I use to assign color to owner is empty it means that it's the first time
        //the program get in this method
        if (sharedData.getFarmColors() != null) {
            farmColorsList = sharedData.getFarmColors();
        }

        switch (field.getFarmName()) {
            case "Bertacchini's Farm":
                if (bertCheck == false) {
                    //In randomColor I get the color which is random generated by the array 'Color'
                    randomColor = getRandomColor("Bertacchini");
                    //in finalColor I get the color which I know was never assigned to any company
                    //If 'Bertacchini' already exist I return the color which it has already
                    finalColor = checkColorIfExist("Bertacchini", randomColor);

                    //polygon.getOutlinePaint().setColor(finalColor);
                    //filling the polygon
                    polygon.setFillColor(finalColor);
                    //this company has now the color assigned, it means that the program doesn't have to
                    //get in this statement case, setting boolean variable to true!
                    sharedData.setCheckIfAlreadyAssignedBert();
                } else {
                    finalColor = checkColorIfExist("Bertacchini", 0);
                    polygon.getOutlinePaint().setColor(finalColor);
                    polygon.setFillColor(finalColor);
                }
                break;

            case "Ferrari's Farm":
                if (ferrCheck == false) {
                    randomColor = getRandomColor("Ferrari");
                    finalColor = checkColorIfExist("Ferrari", randomColor);
                    polygon.getOutlinePaint().setColor(finalColor);
                    polygon.setFillColor(finalColor);
                    sharedData.setCheckIfAlreadyAssignedFerr();
                } else {
                    finalColor = checkColorIfExist("Ferrari", 0);
                    polygon.getOutlinePaint().setColor(finalColor);
                    polygon.setFillColor(finalColor);
                }
                break;
            default:
        }

        polygon.getOutlinePaint().setStrokeWidth(5);
        polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorPrimaryDark));
        this.getOverlayManager().add(polygon);
        this.invalidate();

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
            for (int i = 0; i < farmColorsList.size(); i++) {
                if (randomElement == farmColorsList.get(i).getIdColor()) {
                    check = 1;
                }
            }

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
            Log.d("FarmColorToString", farmColorsList.toString());
        }
    }
}
