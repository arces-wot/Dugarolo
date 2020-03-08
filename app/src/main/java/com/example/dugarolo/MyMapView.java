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

public class MyMapView extends MapView  {

    FarmColor[] FarmColor = {
            new FarmColor(getResources().getColor(R.color.colorBertacchini)),
            new FarmColor(getResources().getColor(R.color.colorFerrari)),
            new FarmColor(getResources().getColor(R.color.colorAccepted)),
            new FarmColor(getResources().getColor(R.color.colorPrimaryDark))
            //da inserire più colori, tanti quante sono le aziende operative
    };

    Random random = new Random();
    int min=0, max=FarmColor.length-1 ,c=0;

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drawWeirs(final ArrayList<Weir> weirs, ArrayList<Marker> weirMarkers) {
        for(Weir weir : weirs) {
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

    public GeoPoint midPoint(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        return GeoPoint.fromCenterBetween(geoPoint1, geoPoint2);
    }

    private void drawField(Field field) {
        ArrayList<GeoPoint> fieldArea = field.getArea();
        Polygon polygon = new Polygon();

        for(GeoPoint point : fieldArea) {
            polygon.addPoint(point);
        }

        int randomColor;

        switch(field.getFarmName()) {
            case "Bertacchini's Farm":
                //polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorBertacchini));
                randomColor = getRandomColor("Bertacchini");
                polygon.getOutlinePaint().setColor(randomColor);
                polygon.setFillColor(randomColor);
                break;
            case "Ferrari's Farm":
                randomColor = getRandomColor("Ferrari");
                polygon.getOutlinePaint().setColor(randomColor);
                polygon.setFillColor(randomColor);
                break;
            default:
        }
        polygon.getOutlinePaint().setStrokeWidth(3);
        this.getOverlayManager().add(polygon);
        this.invalidate();
    }

    public void drawFarms(ArrayList<Farm> farms) {
        for(Farm farm : farms) {
            ArrayList<Field> farmFields = farm.getFields();
            for(Field field : farmFields) {
                this.drawField(field);
            }
        }

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
        for(Marker textMarker : textMarkers) {
            this.getOverlayManager().add(textMarker);
            this.invalidate();
        }
    }

    public int getRandomColor(String nameFarm) {

        int check=0;

        for(int i=0; i<FarmColor.length; i++) {
            if (nameFarm.equals(FarmColor[i].nameFarm)){
                check=i;
            }
        }

        if (check==0) {
            c = ((max - min) + 1);

            int randomElement = random.nextInt(c) + min;

            if (FarmColor[randomElement].usedColor == true) {
                while (FarmColor[randomElement].usedColor == false) {
                    randomElement = random.nextInt(c) + min;
                    Log.d("randomElementFor", Integer.toString(randomElement));
                }
                FarmColor[randomElement].usedColor = true;
            } else {
                FarmColor[randomElement].usedColor = true;
            }

            FarmColor[randomElement].nameFarm = nameFarm;
            return FarmColor[randomElement].idColor;

        }else{
            return FarmColor[check].idColor;
        }

    }

    public void toStringFarmColor(){
        for (int i=0; i<FarmColor.length; i++){
            Log.d("FarmColorToString", FarmColor.toString());
        }
    }
}
