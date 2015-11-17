package br.com.compreingressos;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.EspetaculosAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.logger.CrashlyticsLogger;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Espetaculos;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.ConnectionUtils;
import br.com.compreingressos.utils.GPSTracker;
import br.com.compreingressos.widget.RecyclerViewCustom;

/**
 * Created by luiszacheu on 01/04/15.
 */
public class EspetaculosActivity extends AppCompatActivity implements LocationListener{

    public static final String URL = "http://tokecompre-ci.herokuapp.com/espetaculos.json";
    private static final String OBJ_LIST = "OBJ_LIST";

    // Numero de colunas a ser mostrada na recyclerView
    public static final int COLUMN_NUMBER = 2;
    private static final String LOG_TAG = EspetaculosActivity.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerViewCustom recyclerView;

    private RequestQueue requestQueue;

    private ArrayList<Espetaculo> espetaculos = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout retryConnectionView;

    private GridLayoutManager gridLayoutManager;

    String genero;
    String latitude, longitude;

    GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espetaculos);

        if (getIntent().hasExtra("genero")) {
            genero = getIntent().getStringExtra("genero");
        }

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(genero);

            if (Build.VERSION.SDK_INT >= 21) {
                this.setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark);
                toolbar.setBackgroundColor(getResources().getColor(R.color.red_compreingressos));
                getWindow().setStatusBarColor(getResources().getColor(R.color.red_status_bar));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            } else {
                toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            }


            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Tracker mTracker = ((CompreIngressosApplication) getApplication()).getTracker();
        mTracker.setScreenName(ConstantsGoogleAnalytics.ESPETACULOS.replace("<#>", genero));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        retryConnectionView = (LinearLayout) findViewById(R.id.retry_connection);
        recyclerView = (RecyclerViewCustom) findViewById(R.id.recycler_view_espetaculos);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        this.requestQueue = VolleySingleton.getInstance(EspetaculosActivity.this).getRequestQueue();

        gridLayoutManager = new GridLayoutManager(EspetaculosActivity.this, COLUMN_NUMBER);


        showRecyclerEspetaculosView();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            gpsTracker.stopGPSTracker();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showRecyclerEspetaculosView() {

        gpsTracker = new GPSTracker(EspetaculosActivity.this, this);

        try {
            if (gpsTracker.canGetLocation()){
                latitude =  gpsTracker != null ? Double.toString(gpsTracker.getLatitude()) : "0.0";
                longitude = gpsTracker != null ? Double.toString(gpsTracker.getLongitude()) : "0.0";
            }
        }catch (Exception e){
            CrashlyticsLogger.logException(e);
            CrashlyticsLogger.log("lat " + gpsTracker.getLatitude() + "long " + gpsTracker.getLongitude());
        }



        if (ConnectionUtils.isInternetOn(EspetaculosActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            retryConnectionView.setVisibility(View.GONE);

            startRequest();

        } else {
            retryConnectionView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void setResultAdapter(ArrayList<Espetaculo> listTablet) {
        EspetaculosAdapter adapter = new EspetaculosAdapter(this, listTablet);

        adapter.setOnItemClickListener(onItemClick);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(EspetaculosActivity.this, CompreIngressosActivity.class);
            intent.putExtra("u", espetaculos.get(position).getUrl());
            intent.putExtra("titulo_espetaculo", espetaculos.get(position).getTitulo());
            startActivity(intent);

        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Response.Listener<Espetaculos> createSuccessListener() {
        return new Response.Listener<Espetaculos>() {

            @Override
            public void onResponse(Espetaculos response) {
                if (response.getEspetaculos().size() > 0) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                for (Espetaculo espetaculo : response.getEspetaculos()) {
                    espetaculos = response.getEspetaculos();
                }
                setResultAdapter(espetaculos);
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) EspetaculosActivity.this.findViewById(android.R.id.content)).getChildAt(0);
                Snackbar.make(viewGroup, R.string.snackbar_text, Snackbar.LENGTH_LONG).show();
                if (error instanceof TimeoutError) {
                    CrashlyticsLogger.logException(error);
                } else if (error instanceof NetworkError) {
                    CrashlyticsLogger.logException(error);
                } else if (error instanceof NoConnectionError) {
                    CrashlyticsLogger.logException(error);
                }
            }
        };
    }


    private void startRequest() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        StringBuilder urlCustom = new StringBuilder(URL);

        urlCustom.append("?os=android");

        if (genero.contains("Perto de mim")) {
            urlCustom.append("&latitude=" + latitude + "&longitude=" + longitude);
        } else if (genero.contains("Shows")) {
            urlCustom.append("&genero=Show" + "&latitude=" + latitude + "&longitude=" + longitude);
        } else if (genero.contains("Clássicos")) {
            urlCustom.append("&genero=Classicos" + "&latitude=" + latitude + "&longitude=" + longitude);
        } else if (genero.contains("Teatros")) {
            urlCustom.append("&genero=Teatros" + "&latitude=" + latitude + "&longitude=" + longitude);
        }


        GsonRequest<Espetaculos> jsonObjRequest = new GsonRequest<>(Request.Method.GET, urlCustom.toString(), Espetaculos.class, headers, this.createSuccessListener(), this.createErrorListener(), "yyyy-MM-dd");
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickRetryListener(View view) {
        showRecyclerEspetaculosView();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (gpsTracker.fixLocation(location)){
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);
            Snackbar.make(viewGroup, R.string.message_text_update_location, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_text_button_location, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showRecyclerEspetaculosView();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.green_500))
                    .show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}


