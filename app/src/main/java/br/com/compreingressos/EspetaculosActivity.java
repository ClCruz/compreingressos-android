package br.com.compreingressos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.EspetaculosAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Espetaculos;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.ConnectionUtils;

/**
 * Created by luiszacheu on 01/04/15.
 */
public class EspetaculosActivity extends ActionBarActivity {

    public static final String URL = "http://tokecompre-ci.herokuapp.com/espetaculos.json";

    //    Numero de colunas a ser mostrada na recyclerView
    public static final int COLUMN_NUMBER = 2;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private RequestQueue requestQueue;

    private ArrayList<Espetaculo> espetaculos = new ArrayList<>();
    private ProgressBar progressBar;
    private static final String OBJ_LIST = "OBJ_LIST";
    String genero;
    String latitude, longitude;


    private LinearLayout retryConnectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espetaculos);


        if (getIntent().hasExtra("genero")){
            genero = getIntent().getStringExtra("genero");
        }

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(genero);
            toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Tracker t = ((CompreIngressosApplication) getApplication()).getTracker(CompreIngressosApplication.TrackerName.APP_TRACKER);
        t.enableAutoActivityTracking(true);
        t.setScreenName(ConstantsGoogleAnalytics.ESPETACULOS.replace("<#>", genero));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_espetaculos);
        recyclerView.setHasFixedSize(true);

        showRecyclerEspetaculosView();

    }

    private void showRecyclerEspetaculosView() {
        if (ConnectionUtils.isInternetOn(EspetaculosActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            retryConnectionView.setVisibility(View.GONE);

            recyclerView.setHasFixedSize(true);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(EspetaculosActivity.this, COLUMN_NUMBER);
            recyclerView.setLayoutManager(gridLayoutManager);
            this.requestQueue = VolleySingleton.getInstance(EspetaculosActivity.this).getRequestQueue();
            startRequest();
        }else{
            retryConnectionView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void setResultAdapter(ArrayList<Espetaculo> listTablet) {

        EspetaculosAdapter adapter = new EspetaculosAdapter(this, listTablet);

        adapter.setOnItemClickListener(onItemClick);
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
                Toast.makeText(EspetaculosActivity.this, getResources().getString(R.string.message_sem_conexao), Toast.LENGTH_SHORT).show();
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
}
