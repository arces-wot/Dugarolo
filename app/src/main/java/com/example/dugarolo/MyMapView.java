package com.example.dugarolo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MyMapView extends MapView  {

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drawWeirs(final ArrayList<Weir> weirs, ArrayList<Marker> weirMarkers) {
        for(Weir weir : weirs) {
            Marker marker = new Marker(this);
            marker.setPosition(weir.getPosition());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Drawable weirIcon = getResources().getDrawable(R.drawable.weir);
            Bitmap bitmap = ((BitmapDrawable) weirIcon).getBitmap();
            Drawable resizedWeirIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));
            marker.setIcon(resizedWeirIcon);
            marker.setInfoWindow(null);
            marker.setId(weir.getNumber());
            weirMarkers.add(marker);
            this.getOverlays().add(marker);
            this.invalidate();
        }
    }

    public GeoPoint midPoint(GeoPoint geoPoint1, GeoPoint geoPoint2) {
        return GeoPoint.fromCenterBetween(geoPoint1, geoPoint2);
    }

    public void drawFarms(ArrayList<Farm> farms) {
        //disegna una linea per ogni canale
        for (Farm farm : farms) {
            Polygon polygon = farm.getArea();
            switch (farm.getName()) {
                case "Bertacchini's Farm":
                    polygon.getOutlinePaint().setColor(Color.YELLOW);
                    break;
                case "Ferrari's Farm":
                    polygon.getOutlinePaint().setColor(Color.BLUE);
                    break;
                default:
            }
            polygon.getFillPaint().setColor(Color.TRANSPARENT);
            polygon.getOutlinePaint().setStrokeWidth(3);
            this.getOverlayManager().add(polygon);
            this.invalidate();
        }
    }

    public void drawCanals(ArrayList<Canal> canals, ArrayList<Marker> textMarkers) {
        for(Canal canal : canals) {
            Polyline line = new Polyline();
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(canal.getStart());
            geoPoints.add(canal.getEnd());
            line.setPoints(geoPoints);
            line.getOutlinePaint().setColor(Color.parseColor("#ADD8E6"));
            this.getOverlayManager().add(line);
        }
        for (Marker textMarker : textMarkers) {
            this.getOverlayManager().add(textMarker);
        }
        this.invalidate();
    }
}
