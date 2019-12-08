package com.example.dugarolo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

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
                for (int index = 0; index < JSONArrayFarms.length(); index++) {
                    ArrayList<Field> fields = new ArrayList<>();
                    JSONObject JSONObjectFarm = JSONArrayFarms.getJSONObject(index);
                    JSONArray JSONArrayFields = JSONObjectFarm.getJSONArray("fields");
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
                        JSONObject openLevel = jsonArrayElem.getJSONObject("openLevel");
                        int max = openLevel.getInt("max");
                        int min = openLevel.getInt("min");
                        int current = openLevel.getInt("current");
                        GeoPoint geoPoint = new GeoPoint(jsonArrayElem.getJSONObject("location").getDouble("lat"),
                                jsonArrayElem.getJSONObject("location").getDouble("lon"));
                        Weir weir = new Weir(id, max, min, current, geoPoint);
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

    public void loadRequests(ArrayList<Farm> farms, ArrayList<Request> requests) {
        if(requests.isEmpty()) {
            try {
                for (Farm farm : farms) {
                    ArrayList<Field> fields = farm.getFields();
                    for (Field field : fields) {
                        JSONArray jsonArray = new JSONArray(getJSONFromURL(new URL("http://mml.arces.unibo.it:3000/v0/WDmanager/{id}/WDMInspector/{ispector}/AssignedFarms/{field}/irrigation_plan")));
                        for(int index = 0; index < jsonArray.length(); index++) {
                            JSONObject JSONRequest = jsonArray.getJSONObject(index);
                            String dateTime = JSONRequest.getString("start");
                            DateTime formattedDateTime = DateTime.parse(dateTime);
                            Integer waterVolume = JSONRequest.getInt("waterVolume");
                            Request request = new Request(field.getFarmName(), formattedDateTime, "unknown", waterVolume.toString());
                            requests.add(request);
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

