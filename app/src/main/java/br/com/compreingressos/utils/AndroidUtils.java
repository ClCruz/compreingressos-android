package br.com.compreingressos.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class AndroidUtils {

    public static void enableLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }


    public static boolean isKitKatOrNewer() {
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= Build.VERSION_CODES.KITKAT) {
            return true;
        }
        return false;
    }

    public static boolean isHoneyCombOrNewer(){
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= Build.VERSION_CODES.HONEYCOMB_MR1) {
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

    public static int getWidthScreen(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static boolean isPhablet(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;

        if (heightPixels >= 2000){
            return true;
        }else{
            return false;
        }
    }

    public static void copyAppDbToDownloadFolder(Context context) throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "compreingressos.db"); // for
        // example
        // "my_data_backup.db"
        File currentDB = context.getDatabasePath("compreingressos.sqlite"); // databaseName=your
        // current application database name, for example "my_data.db".
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Log.e("---------------" , "Copiou");
        }
    }


    public static int getDPI(int size, DisplayMetrics metrics) {
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }
}
