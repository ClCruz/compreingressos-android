package br.com.compreingressos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zaca on 6/2/15.
 */
public class ConnectionUtils {

    public static String getTypeNameConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            return connManager.getActiveNetworkInfo().getTypeName();
        }catch (Exception e){
            return "";
        }
    }


    public static boolean isInternetOn(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                || connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
}
