package br.com.compreingressos.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by luiszacheu on 23/10/15.
 */
public class GPSTracker implements LocationListener {

    private static final String LOG_TAG = GPSTracker.class.getSimpleName();
    private final Context context;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    boolean canGetLocation = false;

    Location location;
    Double latitude;
    Double longitude;

    // Minima distancia para fazer update (em metros)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // Minima tempo entre as atualizações (em milisegundos)
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;


    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
        Log.e("initialize gsptracker", "initialize gsptracker");
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            //Habilita provider de GPS
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e(LOG_TAG, "isGPSEnable -> " + isGPSEnable);

            //Habilita provider de Network
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e(LOG_TAG, "isNetworkEnable -> " + isNetworkEnable);

            if (isGPSEnable && isNetworkEnable) {
                this.canGetLocation = true;

                initNetworkProvider();
                initGPSProvider();


            } else if (isNetworkEnable){
                initNetworkProvider();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return location;
    }

    private void initGPSProvider() {
        // habilitando location usando o provider gps
        if (isGPSEnable) {
            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.e(LOG_TAG, "gps");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }
    }

    private void initNetworkProvider() {
        // habilitando location usando o provider network
        if (isNetworkEnable) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.e(LOG_TAG, "network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    //Parar o listener do GPS
    public void stopGPSTracker(){
        if (locationManager != null){
            locationManager.removeUpdates(this);
            Log.e(LOG_TAG, "stop GPSTracker");
        }
    }

    public double getLatitude(){
        if (location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public void showSettingsAlert(String message, String title){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setMessage(message);
        alertBuilder.setTitle(title);
        alertBuilder.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AndroidUtils.enableLocationSettings(context);

                return;
            }
        });

        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
                return;
            }
        });
        alertBuilder.show();

    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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
