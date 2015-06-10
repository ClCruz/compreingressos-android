package br.com.compreingressos;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.support.ConnectionSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.AndroidUtils;
import br.com.compreingressos.utils.ConnectionUtils;
import br.com.compreingressos.utils.DatabaseManager;
import br.com.compreingressos.utils.Dialogs;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener{

    public static final String URL_VISORES = "http://tokecompre-ci.herokuapp.com/visores/lista.json";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private LocationManager mLocationManager;
    private Location mLastLocation;
    private boolean enableLocationGPS;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;


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
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        mLocationManager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        buildGoogleApiClient();

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        startRequest();


        mListGeneros = initGeneros();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_generos);
        adapter = new GeneroAdapter(this, mListGeneros, mListBanners);
        adapter.SetOnItemClickListener(new GeneroAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(View v, int position) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, EspetaculosActivity.class);
                    intent.putExtra("genero", mListGeneros.get(position).getNome().toString());
                    intent.putExtra("latitude", "" + latitude);
                    intent.putExtra("longitude", "" + longitude);
                    enableLocationGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!enableLocationGPS) {
                        Dialogs.showDialogLocation(MainActivity.this, MainActivity.this, getString(R.string.message_dialog_gps),
                                getString(R.string.title_dialog_gps), getString(R.string.btn_gps_positive), getString(R.string.btn_gps_negative), intent);

                    } else {
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, EspetaculosActivity.class);
                    intent.putExtra("genero", mListGeneros.get(position).getNome().toString());
                    intent.putExtra("latitude", "" + latitude);
                    intent.putExtra("longitude", "" + longitude);
                    startActivity(intent);
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);

        DatabaseManager.init(this);

        Tracker t = ((CompreIngressosApplication) getApplication()).getTracker(CompreIngressosApplication.TrackerName.APP_TRACKER);
        t.enableAutoActivityTracking(true);
        t.setScreenName(ConstantsGoogleAnalytics.HOME);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Genero> initGeneros() {
        ArrayList<Genero> generos = new ArrayList<>();
        generos.add(new Genero("Perto de mim", R.drawable.perto_de_mim));
        generos.add(new Genero("Shows", R.drawable.shows));
        generos.add(new Genero("Clássicos", R.drawable.classicos));
        generos.add(new Genero("Teatros", R.drawable.teatro));
        generos.add(new Genero("Muito mais", R.drawable.muito_mais));


        return generos;
    }

    private Response.Listener<Banner[]> createSuccessListener() {
        return new Response.Listener<Banner[]>() {

            @Override
            public void onResponse(Banner[] response) {

                if (response != null) {
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
        StringBuilder urlwithParams = new StringBuilder(URL_VISORES);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        String con = "";

        try {
            if (ConnectionUtils.getTypeNameConnection(MainActivity.this).equals("WIFI")) {
                con = "&con=wifi";
            } else if (ConnectionUtils.getTypeNameConnection(MainActivity.this).equals("mobile")) {
                con = "&con=wwan";
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.message_sem_conexao), Toast.LENGTH_SHORT).show();
        }

        urlwithParams.append("?os=android");
        urlwithParams.append(con);
        urlwithParams.append("&width=" + AndroidUtils.getWidthScreen(MainActivity.this));

        GsonRequest<Banner[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, urlwithParams.toString(), Banner[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
        jsonObjRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(15000, com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);

    }

    private ConnectionSource getConnectionSource() {
        return new DatabaseHelper(MainActivity.this).getConnectionSource();
    }

    /**  * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.  */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


}
