package br.com.compreingressos;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.EspetaculosAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.decoration.DividerItemDecoration;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Espetaculos;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by zaca on 5/8/15.
 */
public class SearchActivity extends ActionBarActivity {

    public static final String URL = "http://tokecompre-ci.herokuapp.com/espetaculos.json?keywords=";

    //Numero de colunas a ser mostrada na recyclerView
    public static final int COLUMN_NUMBER = 2;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private RequestQueue requestQueue;
    private ArrayList<Espetaculo> espetaculos = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView txtNoDataResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espetaculos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            toolbar.setTitle("");
            if (Build.VERSION.SDK_INT >= 21) {
                this.setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark);
                toolbar.setBackgroundColor(getResources().getColor(R.color.red_compreingressos));
                getWindow().setStatusBarColor(getResources().getColor(R.color.red_status_bar));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            }else{
                toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            }
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Tracker mTracker = ((CompreIngressosApplication) getApplication()).getTracker();
        mTracker.setScreenName(ConstantsGoogleAnalytics.BUSCA);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_espetaculos);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, COLUMN_NUMBER);
        recyclerView.setLayoutManager(gridLayoutManager);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        txtNoDataResult = (TextView) findViewById(R.id.no_data_result);

        this.requestQueue = VolleySingleton.getInstance(SearchActivity.this).getRequestQueue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        //Implementação
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setQueryHint("Buscar por nome");
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setFocusable(true);


        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startRequest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        try {
            TextView textView = (TextView) searchView.findViewById(R.id.search_src_text);
            textView.setTextColor(getResources().getColor(R.color.white));

        }catch (Exception e){
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        return true;
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

    private void setResultAdapter(ArrayList<Espetaculo> listTablet){

        EspetaculosAdapter adapter = new EspetaculosAdapter(this, listTablet);

        adapter.setOnItemClickListener(onItemClick);
        recyclerView.setAdapter(adapter);
    }

    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(SearchActivity.this, CompreIngressosActivity.class);
            intent.putExtra("u", espetaculos.get(position).getUrl());
            intent.putExtra("titulo_espetaculo", espetaculos.get(position).getTitulo());
            startActivity(intent);

        }
    };

    private Response.Listener<Espetaculos> createSuccessListener() {


        return new Response.Listener<Espetaculos>() {


            @Override
            public void onResponse(Espetaculos response) {

                if (response.getEspetaculos().size() > 0){

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    for (Espetaculo espetaculo : response.getEspetaculos()){
                        espetaculos = response.getEspetaculos();
                    }

                }else{
                    progressBar.setVisibility(View.GONE);
                    txtNoDataResult.setVisibility(View.VISIBLE);
                    espetaculos.clear();
                }

                setResultAdapter(espetaculos);
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this, "Houve um erro!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void startRequest(String query) {
        progressBar.setVisibility(View.VISIBLE);
        txtNoDataResult.setVisibility(View.GONE);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        StringBuilder urlCustom = new StringBuilder(URL);
        urlCustom.append(query);
        GsonRequest<Espetaculos> jsonObjRequest = new GsonRequest<>(Request.Method.GET, urlCustom.toString(), Espetaculos.class, headers, this.createSuccessListener(), this.createErrorListener(), "yyyy-MM-dd");
        this.requestQueue.add(jsonObjRequest);
    }
}
