package com.example.dugarolo;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    public static final String REQUEST_STATUS = "id";
    private ArrayList<Farm> farms = new ArrayList<>();
    private MyMapView map;
    private MyMapView map2;
    private MyMapView mapHistory;
    private ArrayList<Request> requests = new ArrayList<>();
    private ArrayList<Request> requestsFiltering = new ArrayList<>();
    private ArrayList<Request> requestsFilteringCheck = new ArrayList<>();
    private ArrayList<Marker> farmerMarkers = new ArrayList<>();
    private ArrayList<Weir> weirs = new ArrayList<>();
    private ArrayList<Canal> canals = new ArrayList<>();
    private FloatingActionButton fab;
    private Button gps;
    private LocationManager locationManager;
    private LocationListener listener;
    private Marker markerPosition;
    private ImageView filterButton;
    private ImageView filteredButton;
    private ImageView orderButton;
    private Switch mySwitch;
    private boolean mySwitchStatus=false;
    private Boolean isTomorrow = false;
    GeoPoint myPosition;
    private GeoPoint startPoint;
    private Context ctx;
    private Intent intentExpandMap;
    private ViewPager viewPager;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);


        loadData();

        startPoint = new GeoPoint(44.778325, 10.720202);
        ctx = getApplicationContext();
        intentExpandMap = new Intent(MainActivity.this, MapDetailActivity.class);
        loadMap(startPoint, ctx, requests);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);


        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), requests, map,map2);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 0) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.info));
                    fab.show();
                    isTomorrow = false;
                    refreshVisibilityMaps();

                } else  if (position == 1){
                    fab.hide();
                    isTomorrow = true;
                    refreshVisibilityMaps();
                }
                else if (position == 2){
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.calendar));
                    fab.show();
                    isTomorrow = true;
                    mapHistory.setVisibility(View.VISIBLE);
                }
            }
        });
        TextView noRequests = findViewById(R.id.no_requests);
        noRequests.setVisibility(View.GONE);


        fab = findViewById(R.id.floatingActionButton);
        fab.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.info));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Planning.class);
                if(viewPager.getCurrentItem() == 2){
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
                else
                    startActivity(intent);
            }
        });

        setGPSButton();


        filterButton = findViewById(R.id.filterButton);
        filteredButton = findViewById(R.id.filteredButton);
        filteredButton.setVisibility(View.GONE);
        orderButton = findViewById(R.id.orderButton);
        final Boolean[] isFiltered = {false};
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTomorrow)
                    if (!isFiltered[0]) {
                        requestsFiltering.clear();
                        requestsFiltering.addAll(requests);
                        FilterHandler filterHandler = new FilterHandler();
                        requestsFilteringCheck = filterHandler.buildFilterDialog(MainActivity.this, requestsFiltering);
                        isFiltered[0] = true;
                        filterButton.setVisibility(View.GONE);
                        filteredButton.setVisibility(View.VISIBLE);
                    }
            }
        });
        filteredButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                TodayTab.setChanged(requests);
                filterButton.setVisibility(View.VISIBLE);
                filteredButton.setVisibility(View.GONE);
                isFiltered[0] = false;
            }
        });


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTomorrow) {
                    OrderHandler orderHandler = new OrderHandler();
                    orderHandler.buildOrderDialog(MainActivity.this, requests);
                }
            }
        });


        mySwitch = findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySwitchStatus=isChecked;
                if (isChecked) {
                    map2.setVisibility(View.INVISIBLE);
                    map.setVisibility(View.VISIBLE);
                } else {
                    map2.setVisibility(View.VISIBLE);
                    map.setVisibility(View.INVISIBLE);
                }

            }

        });


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    public void setGPSButton() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps = findViewById(R.id.GPSbutton);

        listener = new LocationListener() {


            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

            @Override
            public void onLocationChanged(Location location) {
                Marker newMarkerPosition;
                if (markerPosition != null)
                    markerPosition.remove(map);
                myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                newMarkerPosition = map.drawPosition(myPosition);
                newMarkerPosition = map2.drawPosition(myPosition);
                markerPosition = newMarkerPosition;
                //map.getController().setCenter(myPosition);
                map2.getController().animateTo(myPosition);
                map.getController().animateTo(myPosition);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        gps.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //   int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 10, listener);
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadMap(GeoPoint startPoint, Context ctx ,ArrayList<Request> requestsList) {
        //load/initialize the osmdroid configuration, this can be done

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);
        map2 = findViewById(R.id.map2);
        mapHistory = findViewById(R.id.mapHistory);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map2.setTileSource(TileSourceFactory.MAPNIK);
        map2.setMultiTouchControls(true);
        map2.setTilesScaledToDpi(true);
        map2.setClickable(true);
        map2.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapHistory.setTileSource(TileSourceFactory.MAPNIK);
        mapHistory.setMultiTouchControls(true);
        mapHistory.setTilesScaledToDpi(true);
        mapHistory.setClickable(true);
        mapHistory.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(14.0);
        mapController.setCenter(startPoint);
        IMapController mapController2 = map2.getController();
        mapController2.setZoom(14.0);
        mapController2.setCenter(startPoint);
        IMapController mapControllerHistory = mapHistory.getController();
        mapControllerHistory.setZoom(14.0);
        mapControllerHistory.setCenter(startPoint);
        for (Request r : requestsList)
            map2.drawField(r.getField(), requestsList);
        map.drawFarms(farms,requestsList);
        for (Request r: requestsList)
            map.drawField(r.getField(),requestsList);
        //map.drawIcon(farms, farmerMarkers, 70);
        //map.drawCanals(canals);
        //map.drawWeirs(weirs,weirMarkers);

    }


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onStop() {
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();
    }

    public void onClickExpandMap(View view) {
        GeoPoint center= (GeoPoint) map.getMapCenter();
        intentExpandMap.putExtra("CENTER", (Parcelable) center);
        intentExpandMap.putExtra("SWITCH_STATUS", mySwitchStatus);
        intentExpandMap.putExtra("VIEWPAGER_POSITION", viewPager.getCurrentItem());
        startActivity(intentExpandMap);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonFarms = gson.toJson(farms);
        String jsonRequests = gson.toJson(requests);
        String jsonWeirs = gson.toJson(weirs);
        String jsonCanals = gson.toJson(canals);
        editor.putString("FARMS", jsonFarms);
        editor.putString("REQUESTS", jsonRequests);
        editor.putString("WEIRS", jsonWeirs);
        editor.putString("CANALS", jsonCanals);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonFarms = sharedPreferences.getString("FARMS", null);
        String jsonRequests = sharedPreferences.getString("REQUESTS", null);
        String jsonWeirs = sharedPreferences.getString("WEIRS", null);
        String jsonCanals = sharedPreferences.getString("CANALS", null);
        Type typeFarm = new TypeToken<ArrayList<Farm>>() {
        }.getType();
        Type typeRequest = new TypeToken<ArrayList<Request>>() {
        }.getType();
        Type typeWeir = new TypeToken<ArrayList<Weir>>() {
        }.getType();
        Type typeCanal = new TypeToken<ArrayList<Canal>>() {
        }.getType();
        farms = gson.fromJson(jsonFarms, typeFarm);
        requests = gson.fromJson(jsonRequests, typeRequest);
        weirs = gson.fromJson(jsonWeirs, typeWeir);
        canals = gson.fromJson(jsonCanals, typeCanal);
    }

   /* public TabsPagerAdapter getTabsPagerAdapter(ArrayList<Request> requests, MyMapView map) {
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager(),
                requests, map);
        return tabsPagerAdapter;
    }*/

    public ViewPager getViewPager(TabsPagerAdapter tabsPagerAdapter) {
         ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        return viewPager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ArrayList<Request> requestsDatePicker = new ArrayList<>();
        requestsDatePicker.addAll(requests);
        requestsDatePicker.removeIf(request ->{
            DateTime dateR = request.getDateTime();
            int dayR = dateR.getDayOfMonth();
            int monthR = dateR.getMonthOfYear();
            int yearR = dateR.getYear();
            if(dayR == dayOfMonth && monthR == month+1 && yearR == year)
                return false;
            else
                return true;
        });//!(request.getDateTime().equals(c)));
        mapHistory.clear();
        mapHistory.getController().scrollBy(0,0);
        for (Request r: requestsDatePicker)
            mapHistory.drawField(r.getField(),requestsDatePicker);

        intentExpandMap.putExtra("R_DATE_PICKER", requestsDatePicker);
        HistoryTab.setChanged(requestsDatePicker);
    }

    public void refreshVisibilityMaps(){
        mapHistory.setVisibility(View.GONE);
        if(mySwitch.isChecked()){
            map.setVisibility(View.VISIBLE);
            map2.setVisibility(View.GONE);
        }
        else{
            map2.setVisibility(View.VISIBLE);
            map.setVisibility(View.GONE);
        }
    }

}

