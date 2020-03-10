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

    int[] FarmColor = {(getResources().getColor(R.color.colorBertacchini)),
                        (getResources().getColor(R.color.colorFerrari))
                        //(getResources().getColor(R.color.colorAccepted)),
                        //(getResources().getColor(R.color.colorPrimaryDark))
            //da inserire più colori, tanti quante sono le aziende operative
    };

    ArrayList<FarmColor> farmColorsList = new ArrayList<FarmColor>();

    Random random = new Random();
    int min=0, max=FarmColor.length-1 , c=((max - min) + 1);

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

            for (GeoPoint point : fieldArea) {
                polygon.addPoint(point);
            }

            fillFarms(field, polygon);
    }

    //solo per riempire
    public void fillFarms(Field field, Polygon polygon) {

        int randomColor, finalColor;

        switch (field.getFarmName()) {
            case "Bertacchini's Farm":
                    //polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorBertacchini));
                    randomColor = getRandomColor("Bertacchini");
                    Log.d("richiestaBert", "richiesta");
                    finalColor = checkColorIfExist("Bertacchini", randomColor);
                    polygon.getOutlinePaint().setColor(finalColor);
                    polygon.setFillColor(finalColor);
                    //Log.d("intervenuto", "vuoto");
                    break;

            case "Ferrari's Farm":
                randomColor = getRandomColor("Ferrari");
                Log.d("richiestaBert", "richiesta");
                finalColor = checkColorIfExist("Ferrari", randomColor);
                polygon.getOutlinePaint().setColor(finalColor);
                polygon.setFillColor(finalColor);
                break;
            default:
        }
        polygon.getOutlinePaint().setStrokeWidth(3);
        polygon.getOutlinePaint().setColor(getResources().getColor(R.color.colorPrimaryDark));
        this.getOverlayManager().add(polygon);
        this.invalidate();

    }

    public void drawFarms(ArrayList<Farm> farms) {

        FarmColor farmColor = new FarmColor("", 0);
        farmColorsList = farmColor.getFarmColors();

        for(Farm farm : farms) {
            ArrayList<Field> farmFields = farm.getFields();
            //Log.d("numeroFarm", Integer.toString(farmFields.size()));
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

    //genero colore
    public int getRandomColor(String nameFarm) {

        int randomElement=0, color=0, finalColor=0;

        //int che sarebbe indice da prendere nell'array vero
        randomElement = random.nextInt(c) + min;
        color = FarmColor[randomElement];

        return color;
    }

    //faccio i controlli
    public int checkColorIfExist(String nameFarm, int randomElement){

        //se la farm non ha un nome allora gli assegno un colore (che non deve essere assegnato a nessun'altro)
        //se il nome è presente in lista allora gli restituisco il colore che gli ho già dato


        if(farmColorsList.size() != 0){

            for(int i=0; i<farmColorsList.size(); i++){
                if(nameFarm.equals(farmColorsList.get(i).getNameFarm())){
                    Log.d("richiesta", nameFarm + "ha già un colore");
                    return farmColorsList.get(i).getIdColor();
                }
            }

            //se arrivi qua vuol dire che non esiste nell'arraylist un campo con questo nome
            //ottenere un colore che non è utilizzato

            int check=0;

            for(int i=0; i<farmColorsList.size(); i++){
                if(randomElement == farmColorsList.get(i).getIdColor()){
                    check=1;
                }
            }

            if(check==0){
                FarmColor farmColor = new FarmColor(nameFarm, randomElement);
                farmColorsList.add(farmColor);
                farmColor.updateList(farmColorsList);

                Log.d("richiesta", nameFarm + "non esisteva e " + randomElement + "non era utilizzato, quindi lo creo");

                return randomElement;
            }else{
                Log.d("richiesta", randomElement + "è già utilizzato");
                getRandomColor(nameFarm);
            }

        }else{
            FarmColor farmColor = new FarmColor(nameFarm, randomElement);
            farmColorsList.add(farmColor);
            farmColor.updateList(farmColorsList);

            return randomElement;
        }
        return 0;
    }

    public void toStringFarmColor(){
        for (int i=0; i<farmColorsList.size(); i++){
            Log.d("FarmColorToString", farmColorsList.toString());
        }
    }
}
