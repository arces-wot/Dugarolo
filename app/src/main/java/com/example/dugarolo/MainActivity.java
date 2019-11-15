package com.example.dugarolo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.danlew.android.joda.JodaTimeAndroid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView requestRecyclerView;
    private RecyclerView.Adapter requestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public static final String REQUEST_STATUS= "id";
    private Integer requestId;
    private ArrayList<Farm> farms = new ArrayList<>();
    private MyMapView map = null;
    private AssetLoader assetLoader = new AssetLoader();
    //debug only
    //private static final String TAG = "MainActivity";



    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        RequestLab requestLab = RequestLab.get(this);
        List<Request> requestList = requestLab.getRequestList();
        loadMap();
        loadRequests(requestList);
        if(requestId != null) {
            Request request = requestList.get(requestId);
            String status = (String) getIntent().getExtras().get(REQUEST_STATUS);
            request.setStatus(status);
        }
    }

    private void loadMap() {
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);
        map = (MyMapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        map.setClickable(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(14.0);
        GeoPoint startPoint = new GeoPoint(44.778325, 10.720202);
        mapController.setCenter(startPoint);
        assetLoader.loadGeoPointsFarms(farms, this);
        map.drawFarms(farms);
    }

    private void loadRequests(List<Request> requestList) {
        Collections.sort(requestList);
        requestRecyclerView = findViewById(R.id.list_requests);
        layoutManager = new LinearLayoutManager(this);
        requestRecyclerView.setLayoutManager(layoutManager);
        requestAdapter = new RequestAdapter(requestList);
        requestRecyclerView.setAdapter(requestAdapter);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onClickExpandMap(View view) {
        Intent intent = new Intent(MainActivity.this, MapDetailActivity.class);
        startActivity(intent);
    }


    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

        private List<Request> requests;

        private class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView farmColor;
            public ImageView basicIcon;
            public TextView dateAndFarmName;
            public ImageView statusIcon;

            public RequestHolder(View itemView) {
                super(itemView);
                farmColor = itemView.findViewById(R.id.farm_color);
                basicIcon = itemView.findViewById(R.id.basic_icon);
                dateAndFarmName = itemView.findViewById(R.id.farm_name);
                statusIcon = itemView.findViewById(R.id.status_icon);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
                intent.putExtra(RequestDetailsActivity.EXTRA_REQUEST_ID, position);
                requestId = position;
                startActivity(intent);
            }
        }

        public RequestAdapter(List<Request> requests) {
                this.requests = requests;
        }

        @NonNull
        @Override
        public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_request, parent, false);
            return new RequestHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull RequestAdapter.RequestHolder holder, int position) {
            Request currentRequest = requests.get(position);
            holder.farmColor.setImageResource(R.drawable.farm_color);
            String name = currentRequest.getName();
            if(name.equals("Bertacchini's farm")) {
                int bertacchini = ContextCompat.getColor(MainActivity.this, R.color.colorBertacchini);
                holder.farmColor.setColorFilter(bertacchini, PorterDuff.Mode.SRC);
            } else if(name.equals("Ferrari's farm")) {
                int ferrari = ContextCompat.getColor(MainActivity.this, R.color.colorFerrari);
                holder.farmColor.setColorFilter(ferrari, PorterDuff.Mode.SRC);
            }
            holder.dateAndFarmName.setText(Html.fromHtml(currentRequest.getName() + "<br />" + "<small>" + currentRequest.getDate().toString() + "</small"));
            String status = currentRequest.getStatus();
            switch (status) {
                case "cancelled":
                    holder.statusIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC);
                    break;
                case "interrupted":
                    holder.statusIcon.setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC);
                    break;
                case "satisfied":
                    holder.statusIcon.setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC);
                    break;
                default:
                    holder.statusIcon.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC);
            }
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }
    }

}
