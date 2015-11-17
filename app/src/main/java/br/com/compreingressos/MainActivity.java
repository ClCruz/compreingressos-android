package br.com.compreingressos;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.decoration.DividerItemDecoration;
import br.com.compreingressos.fragment.MainBannerFragment;
import br.com.compreingressos.helper.ParseHelper;
import br.com.compreingressos.logger.CrashlyticsLogger;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.AndroidUtils;
import br.com.compreingressos.utils.ConnectionUtils;
import br.com.compreingressos.utils.DatabaseManager;
import br.com.compreingressos.utils.Dialogs;
import br.com.compreingressos.widget.RecyclerViewCustom;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public static final String URL_VISORES = "http://tokecompre-ci.herokuapp.com/visores/lista.json";

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";

    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_UPDATED_TIME_STRING_KEY";

    private static final String LOCATION_KEY = "LOCATION_KEY";

    private Toolbar toolbar;

    private LocationManager mLocationManager;
    private Location mLastLocation;
    private boolean enableLocationGPS;

    //Provides the entry point to Google Play services.
    protected GoogleApiClient mGoogleApiClient;

    private ArrayList<Genero> mListGeneros = new ArrayList<>();
    private ArrayList<Banner> mListBanners = new ArrayList<>();

    private RecyclerViewCustom mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private RequestQueue requestQueue;

    private GeneroAdapter adapter;
    private double latitude;
    private double longitude;

    boolean mRequestingLocationUpdates =false;
    private String mLastUpdateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        //Tracking do google analytics, enviando o nome amigave da classe
        Tracker mTracker = ((CompreIngressosApplication) getApplication()).getTracker();
        mTracker.setScreenName(ConstantsGoogleAnalytics.HOME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        buildGoogleApiClient();


        DatabaseManager.init(this);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        mRecyclerView = (RecyclerViewCustom) findViewById(R.id.recycler_view_generos);
        showRecyclerHomeView();

        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    protected void onStart() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0){
            mGoogleApiClient.connect();
        }

        if (ConnectionUtils.getTypeNameConnection(MainActivity.this) != "?" && ConnectionUtils.getTypeNameConnection(MainActivity.this) != "-") {
            startRequest();
        } else {
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            Snackbar
                    .make(viewGroup, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                    .show();

        }
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0){
            if (mGoogleApiClient.isConnected())
                stopLocationUpdates();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        saveInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        saveInstanceState.putParcelable(LOCATION_KEY, mLastLocation);
        super.onSaveInstanceState(saveInstanceState);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history_orders) {
            Intent intent = new Intent(this, HistoryOrdersActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRecyclerHomeView() {
        mListGeneros = initGeneros();
        if (adapter == null) {
            adapter = new GeneroAdapter(this, mListGeneros);
        }

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

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this));
        mRecyclerView.setAdapter(adapter);


    }

    private void startRequest() {
        StringBuilder urlwithParams = new StringBuilder(URL_VISORES);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String con = "";



        try {
            if (ConnectionUtils.getTypeNameConnection(MainActivity.this).equals("WIFI")) {
                con = "&con=wifi";
            } else if (ConnectionUtils.getTypeNameConnection(MainActivity.this) != "?" && ConnectionUtils.getTypeNameConnection(MainActivity.this) != "-") {
                con = "&con=wwan";
            }

        } catch (Exception e) {
            CrashlyticsLogger.logException(e);
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            Snackbar.make(viewGroup, R.string.snackbar_text, Snackbar.LENGTH_LONG).show();
        }

        urlwithParams.append("?os=android");
        urlwithParams.append(con);
        urlwithParams.append("&width=" + AndroidUtils.getWidthScreen(MainActivity.this));

        GsonRequest<Banner[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, urlwithParams.toString(), Banner[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
        jsonObjRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(15000, com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);

    }

    private Response.Listener<Banner[]> createSuccessListener() {
        return new Response.Listener<Banner[]>() {

            @Override
            public void onResponse(Banner[] response) {

                if (response != null) {
                    mListBanners = new ArrayList<>();
                    for (int i = 0; i < response.length; i++) {
                        Banner banner = new Banner();
                        banner.setImagem(response[i].getImagem());
                        banner.setUrl(response[i].getUrl());
                        banner.setTitulo(response[i].getUrl().toString());

                        mListBanners.add(banner);
                    }

                    if (mListBanners != null) {
                        MainBannerFragment bannerFragment = ((MainBannerFragment) getSupportFragmentManager().findFragmentByTag("header"));
                        if (bannerFragment != null) {
                            bannerFragment.updateBannerAdapter(mListBanners);
                        }
                    }
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                final ProgressBar progressBar;
                //Remove o progress bar do placeholder do cabeçalho da lista quando houver algum erro.
                //Verifica se encontra a progresse barr
                if (mRecyclerView != null) {
                    progressBar = (ProgressBar) mRecyclerView.findViewById(R.id.progressBar);
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    //Constroi uma GoogleApiClient. Usar o metodo addApi() para responder o LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * 60 * 1);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private void geocoderToSaveOnParseChannel(Double lat, Double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = new ArrayList<>();
        String temp[] = new String[0];
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0){
                temp = addressList.get(0).getAddressLine(1).split("-");
                if (temp.length > 0)
                    ParseHelper.setSubscribeParseChannelToLocation(temp[1].trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
            CrashlyticsLogger.log("lat -> " + lat + "long -> " + lon);
            CrashlyticsLogger.log("addressList -> " + addressList);
            CrashlyticsLogger.logException(e);
        } catch (Exception e) {
            e.printStackTrace();
            CrashlyticsLogger.log("lat -> " + lat + "long -> " + lon);
            CrashlyticsLogger.log("addressList -> " + addressList);
            CrashlyticsLogger.log("temp -> " + temp);
            CrashlyticsLogger.logException(e);
        }
    }

    protected void startLocationUpdates(){
       LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
    }

    protected  void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mRequestingLocationUpdates){
            startLocationUpdates();
            mRequestingLocationUpdates = true;
        }

        if (mLastLocation != null) {
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            geocoderToSaveOnParseChannel(latitude, longitude);
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

    public ArrayList<Genero> initGeneros() {
        ArrayList<Genero> generos = new ArrayList<>();
        generos.add(new Genero("Perto de mim", R.drawable.ic_perto_de_mim));
        generos.add(new Genero("Shows", R.drawable.ic_show));
        generos.add(new Genero("Clássicos", R.drawable.ic_classico));
        generos.add(new Genero("Teatros", R.drawable.ic_teatro));
        generos.add(new Genero("Muito mais", R.drawable.ic_muito_mais));


        return generos;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0){
            if (savedInstanceState != null) {
                // Update the value of mRequestingLocationUpdates from the Bundle, and
                // make sure that the Start Updates and Stop Updates buttons are
                // correctly enabled or disabled.
                if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                    mRequestingLocationUpdates = savedInstanceState.getBoolean(
                            REQUESTING_LOCATION_UPDATES_KEY);
                }

                // Update the value of mCurrentLocation from the Bundle and update the
                // UI to show the correct latitude and longitude.
                if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                    // Since LOCATION_KEY was found in the Bundle, we can be sure that
                    // mCurrentLocationis not null.
                    mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                }

                // Update the value of mLastUpdateTime from the Bundle and update the UI.
                if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                    mLastUpdateTime = savedInstanceState.getString(
                            LAST_UPDATED_TIME_STRING_KEY);
                }
            }
        }

        showRecyclerHomeView();
    }
}
