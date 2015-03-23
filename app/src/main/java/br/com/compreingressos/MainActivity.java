package br.com.compreingressos;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.Dialogs;


public class MainActivity extends ActionBarActivity implements LocationListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL_ESPETACULOS = "http://www.compreingressos.com/?app=tokecompre";
    private LocationManager locationManager;
    private Location location;
    private boolean hasLocationGPS, hasLocationWifi;

    private Toolbar toolbar;

    private ListView lvGeneros;
    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }



        header =  getLayoutInflater().inflate(R.layout.header_generos, null);

        lvGeneros = (ListView) findViewById(R.id.lv_merchant_generos);
        final GeneroAdapter adapter = new GeneroAdapter(MainActivity.this, initGeneros());
        lvGeneros.addHeaderView(header);
        lvGeneros.setAdapter(adapter);
        lvGeneros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, adapter.getItem(position).getNome().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CompreIngressosActivity.class);
                try {
                    if (adapter.getItem(position-1).getNome().toString().contains("Perto")){
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") +  "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
                    }else{
                        intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre") + "&genero="+ adapter.getItem(position-1).getNome().toString() + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
                    }
                }catch (Exception e){
                    intent.putExtra("url", URL_ESPETACULOS.replace("?app=tokecompre","espetaculos?app=tokecompre"));
                    Log.e(LOG_TAG, "" + e.getMessage());
                }
                intent.putExtra("genero", adapter.getItem(position - 1).getNome().toString());
                startActivity(intent);
            }
        });
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
        generos.add(new Genero("Perto de Mim", R.drawable.perto_de_mim));
        generos.add(new Genero("Concertos Sinfônicos", R.drawable.concerto_sinfonico));
        generos.add(new Genero("Comédia",R.drawable.comedia));
        generos.add(new Genero("Shows",R.drawable.shows));
        generos.add(new Genero("Infantil",R.drawable.infantil));
        generos.add(new Genero("Drama", R.drawable.drama));
        generos.add(new Genero("Stand-Up Comedy", R.drawable.stand_up));
        generos.add(new Genero("Musical", R.drawable.musical));
        generos.add(new Genero("Ópera", R.drawable.opera));
        generos.add(new Genero("Romance", R.drawable.romance));
        generos.add(new Genero("Espírita",R.drawable.espirita));
        generos.add(new Genero("Musical Infantil", R.drawable.musica_infantil));
        generos.add(new Genero("Comédia Musical", R.drawable.comedia_musical));
        generos.add(new Genero("Dança", R.drawable.danca));
        generos.add(new Genero("Comédia Romântica", R.drawable.comedia_romantica));
        generos.add(new Genero("Comédia Dramática", R.drawable.comedia_dramatica));
        generos.add(new Genero("Suspense", R.drawable.suspense));
        generos.add(new Genero("Comédia Perversa", R.drawable.comedia_perversa));
        generos.add(new Genero("Música",R.drawable.musica));
        generos.add(new Genero("Circo", R.drawable.circo));

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
