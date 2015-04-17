package br.com.compreingressos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.adapter.EspetaculosAdapter;
import br.com.compreingressos.decoration.DividerItemDecoration;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Espetaculos;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by luiszacheu on 01/04/15.
 */
public class EspetaculosActivity extends ActionBarActivity {

    public static final String URL = "http://tokecompre-ci.herokuapp.com/espetaculos.json";
    public static final String URL_WEBVIEW = "http://www.compreingressos.com/?app=tokecompre";

//    Numero de colunas a ser mostrada na recyclerView
    public static final int COLUMN_NUMBER = 2;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private RequestQueue requestQueue;

    private ArrayList<Espetaculo> espetaculos = new ArrayList<>();
    private ProgressBar progressBar;
    private static final String OBJ_LIST = "OBJ_LIST";
    String genero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espetaculos);

        if (getIntent().hasExtra("genero")){
            genero = getIntent().getStringExtra("genero");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(genero);
            toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_espetaculos);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(EspetaculosActivity.this, COLUMN_NUMBER);
        recyclerView.setLayoutManager(gridLayoutManager);


        this.requestQueue = VolleySingleton.getInstance(EspetaculosActivity.this).getRequestQueue();
        startRequest();

    }

    private void setResultAdapter(ArrayList<Espetaculo> listTablet){

        EspetaculosAdapter adapter = new EspetaculosAdapter(this, listTablet);

        adapter.setOnItemClickListener(onItemClick);
        recyclerView.setAdapter(adapter);
    }


    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(getApplicationContext(), espetaculos.get(position).getTitulo(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EspetaculosActivity.this, CompreIngressosActivity.class);
            intent.putExtra("url", espetaculos.get(position).getUrl());
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

                for (Espetaculo espetaculo : response.getEspetaculos()){
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
                Toast.makeText(EspetaculosActivity.this, "Houve um erro!", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void startRequest() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        GsonRequest<Espetaculos> jsonObjRequest = new GsonRequest<>(Request.Method.GET, URL, Espetaculos.class, headers, this.createSuccessListener(), this.createErrorListener(), "yyyy-MM-dd");
        this.requestQueue.add(jsonObjRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
