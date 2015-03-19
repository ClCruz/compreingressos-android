package br.com.compreingressos.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class AndroidUtils {

    public static void enableLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }
}
