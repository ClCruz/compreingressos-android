package br.com.compreingressos.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class AndroidUtils {

    public static void enableLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }


    public static boolean isKitKatOrNewer(Context context) {
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= Build.VERSION_CODES.KITKAT) {
            return true;
        }

        return false;
    }

    public static void getMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;
        Log.e("TAG", "densityDpi >> " + densityDpi);

        float density = dm.density;
        Log.e("TAG", "density >> " + density);

        float scaleDensity = dm.scaledDensity;
        Log.e("TAG", "scaleDensity >> " + scaleDensity);

        int heightPixels = dm.heightPixels;
        Log.e("TAG", "heightPixels >> " + heightPixels);

    }

    public static boolean isPhablet(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;
        Log.e("TAG", "heightPixels >> " + heightPixels);
        if (heightPixels >= 2000){
            return true;
        }else{
            return false;
        }
    }
}
