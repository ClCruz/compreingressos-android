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

import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.adapter.GeneroRVArapter;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.Dialogs;


public class MainActivity extends ActionBarActivity implements LocationListener{

    public static final String URL_ESPETACULOS = "http://www.compreingressos.com/?app=tokecompre";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private LocationManager locationManager;
    private Location location;
    private boolean hasLocationGPS, hasLocationWifi;

    private Toolbar toolbar;

    List<Genero> generos = new ArrayList<Genero>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        generos = initGeneros();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_generos);
        GeneroRVArapter adapter = new GeneroRVArapter(MainActivity.this, generos);
        adapter.SetOnItemClickListener(new GeneroRVArapter.OnItemClickListener() {
            @Override
            public void onClickListener(View v, int position) {
                Intent intent = new Intent(MainActivity.this, CompreIngressosActivity.class);
                try {
<<<<<<< HEAD
                    if (adapter.getItem(position-1).getNome().toString().contains("Perto")){
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") +  "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
                    }else{
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") + "&genero="+ adapter.getItem(position-1).getNome().toString() + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
=======
                    if (generos.get(position).getNome().toString().contains("Perto")){
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") +  "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
                    }else{
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") + "&genero="+ generos.get(position).getNome().toString() + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
>>>>>>> 5094167da3562a57d7a50bae24fc7c0be89e10ac
                    }
                }catch (Exception e){
                    intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre"));
                    Log.e(LOG_TAG, "" + e.getMessage());
                }
<<<<<<< HEAD
                intent.putExtra("genero", adapter.getItem(position - 1).getNome().toString());
=======

                intent.putExtra("genero", generos.get(position).getNome().toString());
>>>>>>> 5094167da3562a57d7a50bae24fc7c0be89e10ac
                startActivity(intent);
            }
        });
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this));
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
            if (QMCompreIngressosAplication.getInstance().isDisplayDialogLocation) {
                QMCompreIngressosAplication.getInstance().setDisplayDialogLocation(false);
                Dialogs.showDialogLocation(MainActivity.this, this, getString(R.string.message_dialog_gps),
                        getString(R.string.title_dialog_gps), getString(R.string.btn_gps_positive), getString(R.string.btn_gps_negative));
            }

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

    public List<Genero> initGeneros(){
        List<Genero> generos = new ArrayList<Genero>();
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
}
