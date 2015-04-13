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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.helper.OrderHelper;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Espetaculos;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
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

                startActivity(intent);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);


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

        OrderHelper.loadOrderFromJSON(OrderHelper.JSON);
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
        try {
            Log.e(LOG_TAG, " " + location.getLatitude());
            Log.e(LOG_TAG, "" +  location.getLongitude());
        }catch (Exception e){
            Log.e(LOG_TAG, "" +  e.getMessage());
        }
    }

    public void getNetworkLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 200, 0, this);

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Genero> initGeneros(){
        ArrayList<Genero> generos = new ArrayList<>();
        generos.add(new Genero("Sugestões perto de mim", R.drawable.perto_de_mim));
        generos.add(new Genero("Shows",R.drawable.shows));
        generos.add(new Genero("Classicos", R.drawable.concerto_sinfonico));
        generos.add(new Genero("Teatro",R.drawable.comedia));
        generos.add(new Genero("Muito mais", R.drawable.circo));


        return generos;
    }


    @Override
    public void onLocationChanged(Location location) {

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

                for (int i = 0; i < response.length; i++) {
                    Banner banner = new Banner();
                    banner.setImagem(response[i].getImagem());
                    banner.setUrl(response[i].getUrl());

                    mListBanners.add(banner);

                }

                adapter.updateBanners(mListBanners);
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

        GsonRequest<Banner[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, URL_VISORES, Banner[].class, headers, this.createSuccessListener(), this.createErrorListener());
        this.requestQueue.add(jsonObjRequest);


    }
}
