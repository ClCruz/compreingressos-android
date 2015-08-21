package br.com.compreingressos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crashlytics.android.Crashlytics;

/**
 * Created by zaca on 6/2/15.
 */
public class ConnectionUtils {

    public static String getTypeNameConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            return connManager.getActiveNetworkInfo().getTypeName();
        } catch (Exception e) {
            Crashlytics.logException(e);
            return "";
        }
    }

    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.isConnectedOrConnecting())
                return true;
        }
        return false;

    }
}
