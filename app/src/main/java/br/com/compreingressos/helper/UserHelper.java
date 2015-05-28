package br.com.compreingressos.helper;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by zaca on 5/28/15.
 */
public class UserHelper {

    public static void saveUserIdOnSharedPreferences(Context context, String hash_user){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user", hash_user).commit();
    }

    public static String retrieveUserIdOnSharedPreferences(Context context){

        return (PreferenceManager.getDefaultSharedPreferences(context).getString("user", ""));

    }
}
