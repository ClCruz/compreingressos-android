package br.com.compreingressos.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by luiszacheu on 23/10/15.
 */
public class GPSTracker {

    private static final String LOG_TAG = GPSTracker.class.getSimpleName();

    private static final int FIFTEEN_MINUTES = 1000 * 60 * 15;

    // Minima distancia para fazer update (em metros)
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // Minima tempo entre as atualizações (em milisegundos)
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private final Context context;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    boolean canGetLocation = false;

    Location location;
    Double latitude;
    Double longitude;
    
    protected LocationManager locationManager;

    private LocationListener locationListener;
    long currentTimeBestLocation;

    public GPSTracker(Context context, LocationListener listener) {
        this.context = context;
        this.locationListener = listener;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

            //Habilita provider de GPS
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Habilita provider de Network
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                Log.e(LOG_TAG, "gps");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    currentTimeBestLocation = location.getTime();
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            Log.e(LOG_TAG, "network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                currentTimeBestLocation = location.getTime();
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
            locationManager.removeUpdates(locationListener);
            Log.e(LOG_TAG, "stop GPSTracker");
        }
    }

    public double getLatitude(){
        if (location != null){
            latitude = location.getLatitude();

            return latitude;
        }else{
            return 0;
        }


    }

    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();

            return longitude;
        }else{
            return 0;
        }


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


    public boolean fixLocation(Location mLocation){
        boolean result = false;
        // Verifica se a nova localizacao é mais velha do que a nova
        long timeDelta = mLocation.getTime() - currentTimeBestLocation;
        boolean isSignificantlyNewer = timeDelta > FIFTEEN_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -FIFTEEN_MINUTES;
        boolean isNewer = timeDelta > 0;

//        Se for maior do que 2 minutos desde a ultima localizacao
        if (isSignificantlyNewer) {
            result = true;
        } else if (isSignificantlyOlder) {
            result = false;
        }

        return result;
    }

}
