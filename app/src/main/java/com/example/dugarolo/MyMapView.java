package com.example.dugarolo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        switch(field.getFarmName()) {
            case "Bertacchini's Farm":
                polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorBertacchini));
                break;
            case "Ferrari's Farm":
                polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorFerrari));
                break;
            default:
        }
        polygon.getFillPaint().setColor(Color.TRANSPARENT);
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
        }
    }

    public void drawTextMarkers(ArrayList<Canal> canals, ArrayList<Marker> textMarkers) {
        if(textMarkers.size() > 0) {
            for(Marker textMarker : textMarkers) {
                textMarker.setVisible(false);
            }
        }
        for(Canal canal : canals) {
            Marker marker = new Marker(this);
            marker.setPosition(this.midPoint(canal.getStart(), canal.getEnd()));
            marker.setTextLabelBackgroundColor(Color.TRANSPARENT);
            marker.setTextLabelForegroundColor(Color.RED);
            marker.setTextLabelFontSize(20);
            marker.setTextIcon(canal.getWaterLevel().toString() + " mm");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    //nascondo la info window e impedisco lo zoom-in automatico sul click
                    return true;
                }
            });
            textMarkers.add(marker);
            this.getOverlayManager().add(marker);
            this.invalidate();
        }
    }
}
