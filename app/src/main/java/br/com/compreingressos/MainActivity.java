package br.com.compreingressos;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.support.ConnectionSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.DatabaseManager;
import br.com.compreingressos.utils.Dialogs;


public class MainActivity extends ActionBarActivity implements LocationListener{

    public static final String URL_VISORES = "http://tokecompre-ci.herokuapp.com/visores/lista.json";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private LocationManager locationManager;
    private Location location;
    private boolean hasLocationGPS, hasLocationWifi;

    private Toolbar toolbar;

    ArrayList<Genero> mListGeneros = new ArrayList<>();
    ArrayList<Banner> mListBanners = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private RequestQueue requestQueue;

    private GeneroAdapter adapter;
    private double latitude;
    private double longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        startRequest();


        mListGeneros = initGeneros();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_generos);
        adapter = new GeneroAdapter(this, mListGeneros, mListBanners);
        adapter.SetOnItemClickListener(new GeneroAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(View v, int position) {
                Intent intent = new Intent(MainActivity.this, EspetaculosActivity.class);
                intent.putExtra("genero", mListGeneros.get(position).getNome().toString());
                intent.putExtra("latitude", "" + latitude);
                intent.putExtra("longitude", ""+longitude);

                startActivity(intent);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);

        DatabaseManager.init(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        hasLocationGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        hasLocationWifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (hasLocationGPS) {
            if (location == null) {
                try {
                    getGPSLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                    getNetworkLocation();
                }
            }


        } else {
            if (CompreIngressosApplication.getInstance().isDisplayDialogLocation) {
                CompreIngressosApplication.getInstance().setDisplayDialogLocation(false);
                Dialogs.showDialogLocation(this, this, getString(R.string.message_dialog_gps),
                        getString(R.string.title_dialog_gps), getString(R.string.btn_gps_positive), getString(R.string.btn_gps_negative));
            }

            getNetworkLocation();
        }

        if (location == null){
            getNetworkLocation();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    public void getGPSLocation() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 200, 0, this);

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    public void getNetworkLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history_orders) {
            Intent intent = new Intent(this, HistoryOrdersActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Genero> initGeneros(){
        ArrayList<Genero> generos = new ArrayList<>();
        generos.add(new Genero("Perto de mim", R.drawable.perto_de_mim));
        generos.add(new Genero("Shows",R.drawable.shows));
        generos.add(new Genero("Clássicos", R.drawable.classicos));
        generos.add(new Genero("Teatros", R.drawable.teatro));
        generos.add(new Genero("Muito mais", R.drawable.muito_mais));


        return generos;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Response.Listener<Banner[]> createSuccessListener() {
        return new Response.Listener<Banner[]>() {

            @Override
            public void onResponse(Banner[] response) {

                if (response != null){
                    for (int i = 0; i < response.length; i++) {
                        Banner banner = new Banner();
                        banner.setImagem(response[i].getImagem());
                        banner.setUrl(response[i].getUrl());

                        mListBanners.add(banner);

                    }

                    adapter.updateBanners(mListBanners);
                }


            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("-----> ", error.toString());
            }
        };
    }

    private void startRequest() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        GsonRequest<Banner[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, URL_VISORES, Banner[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
        this.requestQueue.add(jsonObjRequest);

    }

    private ConnectionSource getConnectionSource(){
        return new DatabaseHelper(MainActivity.this).getConnectionSource();
    }


}
