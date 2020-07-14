package com.example.dugarolo;

import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class AssetLoader {

    public AssetLoader() {

    }

    private String getJSONFromURL(URL url) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuffer jsonText = new StringBuffer();

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();


                if (code == 200) {


                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    jsonText.append(line + "\n");
                }

                return jsonText.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void loadGeoPointsFarms(ArrayList<Farm> farms) {
        if (farms.isEmpty()) {
            try {
                JSONArray JSONArrayFarms = new JSONArray(getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{ispector}/assigned_farms")));
                //for (int index = 0; index < JSONArrayFarms.length(); index++) {
                for (int index = 0; index < JSONArrayFarms.length(); index++) {
                    ArrayList<Field> fields = new ArrayList<>();
                    JSONObject JSONObjectFarm = JSONArrayFarms.getJSONObject(index);
                    JSONArray JSONArrayFields = JSONObjectFarm.getJSONArray("fields");
                    /*JSONObject iconPoint = JSONObjectFarm.getJSONObject("location");
                    double latI = iconPoint.getDouble("lat");
                    double lonI = iconPoint.getDouble("lon");
                    GeoPoint geoIconPoint = new GeoPoint(latI, lonI);*/

                    for (int index1 = 0; index1 < JSONArrayFields.length(); index1++) {
                        JSONObject field = JSONArrayFields.getJSONObject(index1);
                        String fieldId = field.getString("id");
                        ArrayList<GeoPoint> fieldPoints = new ArrayList<>();
                        JSONArray JSONFieldPoints = field.getJSONArray("area");
                        for (int index2 = 0; index2 < JSONFieldPoints.length(); index2++) {
                            JSONObject point = JSONFieldPoints.getJSONObject(index2);
                            double lat = point.getDouble("lat");
                            double lon = point.getDouble("lon");
                            GeoPoint geoPoint = new GeoPoint(lat, lon);
                            fieldPoints.add(geoPoint);
                        }
                        fields.add(new Field(JSONObjectFarm.getString("name"), fieldId, fieldPoints));
                    }

                    //Farm farm = new Farm(JSONObjectFarm.getString("name"), fields, geoIconPoint);
                    Farm farm = new Farm(JSONObjectFarm.getString("name"), fields);

                    farms.add(farm);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWDN(ArrayList<Canal> canals) {
        try {

            JSONArray arrayConnections = new JSONArray(getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/wdn/connections")));
            for (int i = 0; i < arrayConnections.length(); i++) {
                JSONObject conn = arrayConnections.getJSONObject(i);
                if (conn.getString("type").equals("Channel")) {

                    String id = conn.getString("id");
                    double geoLanStart = conn.getJSONObject("start").getDouble("lan");
                    double geoLongStart = conn.getJSONObject("start").getDouble("long");
                    GeoPoint start = new GeoPoint(geoLanStart, geoLongStart);
                    double geoLanEnd = conn.getJSONObject("end").getDouble("lan");
                    double geoLongEnd = conn.getJSONObject("end").getDouble("long");
                    GeoPoint end = new GeoPoint(geoLanEnd, geoLongEnd);
                    Canal canal = new Canal(id, start, end, 0);
                    canals.add(canal);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loadGeoPointsCanals(ArrayList<Canal> canals) {
        try {
            if (canals.isEmpty()) {
                JSONArray jsonChannels = new JSONArray(getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/wdn/connections")));
                //ottengo i dati delle "connections"
                for (int index = 0; index < jsonChannels.length(); index++) {
                    JSONObject jsonArrayElem = jsonChannels.getJSONObject(index);
                    //considero solo i canali
                    if (jsonArrayElem.getString("type").equals("Channel")) {
                        String id = jsonArrayElem.getString("id");
                        double geoLanStart = jsonArrayElem.getJSONObject("start").getDouble("lan");
                        double geoLongStart = jsonArrayElem.getJSONObject("start").getDouble("long");
                        GeoPoint start = new GeoPoint(geoLanStart, geoLongStart);
                        double geoLanEnd = jsonArrayElem.getJSONObject("end").getDouble("lan");
                        double geoLongEnd = jsonArrayElem.getJSONObject("end").getDouble("long");
                        GeoPoint end = new GeoPoint(geoLanEnd, geoLongEnd);
                        Integer waterLevel = jsonArrayElem.getInt("waterLevel");
                        Canal canal = new Canal(id, start, end, waterLevel);
                        canals.add(canal);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loadGeoPointsWeirs(ArrayList<Weir> weirs) {
        if(weirs.isEmpty()) {
            try {
                JSONArray jsonArrayWeirs = new JSONArray(getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/wdn/nodes")));
                for (int index = 0; index < jsonArrayWeirs.length(); index++) {
                    JSONObject jsonArrayElem = jsonArrayWeirs.getJSONObject(index);
                    //considero solo le chiuse
                    if (jsonArrayElem.getString("type").equals("Weir")) {
                        String id = jsonArrayElem.getString("id");
                    /*JSONObject openLevel = jsonArrayElem.getJSONObject("openLevel");
                    int max = openLevel.getInt("max");
                    int min = openLevel.getInt("min");
                    int current = openLevel.getInt("current");*/
                        GeoPoint geoPoint = new GeoPoint(jsonArrayElem.getJSONObject("location").getDouble("lat"),
                                jsonArrayElem.getJSONObject("location").getDouble("lon"));
                        Weir weir = new Weir(id, 50, 0, 10, geoPoint);
                        weirs.add(weir);
                    }
                }
                } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateCurrentOpenLevels(ArrayList<Weir> weirs) {
        try {
            for (Weir weir : weirs) {
                //String idForUrl = weir.getId().replace(" ", "%20");
                String idForUrl = weir.getId().replace(":", "%3A");
                idForUrl = idForUrl.replace("/", "%2F");
                idForUrl = idForUrl.replace("#", "%23");
                String openLevel = getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/wdn/nodes/" + idForUrl + "/open_level"));
                openLevel = openLevel.replace("\n", "");
                if (!openLevel.equals("undefined"))
                    weir.setOpenLevel(Integer.parseInt(openLevel));
                else
                    weir.setOpenLevel(1);


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void loadRequests(ArrayList<Farm> farms, ArrayList<Request> requests) {

        if (requests.isEmpty()) {
            try {
                String json = getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{inspector}/irrigation_plan"));
                if (json != null) {
                    JSONArray jsonArray = new JSONArray(json);

                    //////Provo a diminuire il tempo di caricamento
                    DateTime now;
                    now=DateTime.now();

                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject JSONRequest = jsonArray.getJSONObject(index);
                        String dateTime = JSONRequest.getString("start");
                        DateTime formattedDateTime = DateTime.parse(dateTime);
                        if(formattedDateTime.getDayOfYear()!=now.getDayOfYear() && formattedDateTime.getDayOfYear()!=now.getDayOfYear()+1 )
                            jsonArray.remove(index);
                    }
                        /////// fine prova
                for (Farm farm : farms) {
                    ArrayList<Field> fields = farm.getFields();
                    for (Field field : fields) {
                        /*String id = field.getId();
                        String idForUrl = id.replace(":", "%3A");
                        idForUrl = idForUrl.replace("/", "%2F");
                        idForUrl = idForUrl.replace("#", "%23");*/


                            for (int index = 0; index < jsonArray.length(); index++) {
                                JSONObject JSONRequest = jsonArray.getJSONObject(index);

                                String id = JSONRequest.getString("id");


                                String fieldId=JSONRequest.getString("field");
                                if(fieldId.equals(field.getId())){
                                    JSONObject channelOb = JSONRequest.getJSONObject("channel");
                                    String channel = channelOb.getString("id");
                                    String nameChannel = channelOb.getString("name");


                                    String dateTime = JSONRequest.getString("start");
                                    DateTime formattedDateTime = DateTime.parse(dateTime);
                                    Integer waterVolume = JSONRequest.getInt("waterVolume");
                                    String requestName = field.getFarmName();
                                    String status = JSONRequest.getString("status");



                                    String type = JSONRequest.getString("type");
                                    String message = "";

                                    if (JSONRequest.has("message"))
                                        message = JSONRequest.getString("message");

                                    Request request = new Request(id, requestName, formattedDateTime, status, waterVolume.toString(), field, message, channel, type, nameChannel);
                                    requests.add(request);
                                }

                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

