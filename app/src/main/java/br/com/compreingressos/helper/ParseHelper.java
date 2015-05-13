package br.com.compreingressos.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by zaca on 5/11/15.
 */
public class ParseHelper {

    public ParseHelper() {
        // TODO Auto-generated constructor stub
    }

    public static void setSubscribeParseChannel(final String parseChannel) {
        ParsePush.subscribeInBackground(parseChannel, new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("com.parse.push", "successfully subscribed to the broadcast channel." + parseChannel);
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }

            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static String getParseChannel() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    public static void setUnSubscribeParseChannel(final String parseChannel){
        ParsePush.unsubscribeInBackground(parseChannel, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("com.parse.push", "successfully unsubscribed to the broadcast channel." + parseChannel);
                } else {
                    Log.e("com.parse.push", "failed to unsubscribed for push", e);
                }
            }
        });
    }


    public static boolean checkClientSubscribeParseChannel(){
        if (ParseInstallation.getCurrentInstallation().containsKey("client")){
            return true;
        }else{
            return false;
        }
    }


    public static void setIsClient(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isclient", true).commit();
    }

    public static boolean getIsClient(Context context){

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isclient", false)){
            return true;
        }else{
            return false;
        }
    }
}

