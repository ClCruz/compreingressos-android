package br.com.compreingressos.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

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
        String channels = "";
        if (ParseInstallation.getCurrentInstallation().get("channels") != null){
            channels = ParseInstallation.getCurrentInstallation().get("channels").toString();
        }


        return channels;
    }

    public static void setUnSubscribeParseChannel(final String parseChannel) {
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

    public static boolean checkClientSubscribeParseChannel(String channel) {
        if (getParseChannel().isEmpty()){
            return false;
        }else{
            if (getParseChannel().contains(channel)) {
                return true;
            } else {
                return false;
            }
        }

    }


    public static void setIsClient(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isclient", true).commit();
    }

    public static boolean getIsClient(Context context) {

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isclient", false)) {
            return true;
        } else {
            return false;
        }
    }

    public static void setSubscribeParseChannelToLocation(String state) {
        Map<String, String> states = new HashMap<>();

        states.put("AC", "ACRE");
        states.put("AL", "ALAGOAS");
        states.put("AM", "AMAZONAS");
        states.put("AP", "AMAPA");
        states.put("BA", "BAHIA");
        states.put("CE", "CEARA");
        states.put("DF", "DISTRITO_FEDERAL");
        states.put("ES", "ESPIRITO_SANTO");
        states.put("GO", "GOIAS");
        states.put("MA", "MARANHAO");
        states.put("MG", "MINAS_GERAIS");
        states.put("MS", "MATO_GROSSO_DO_SUL");
        states.put("MT", "MATO_GROSSO");
        states.put("PA", "PARA");
        states.put("PB", "PARAIBA");
        states.put("PE", "PERNAMBUCO");
        states.put("PI", "PIAUI");
        states.put("PR", "PARANA");
        states.put("RJ", "RIO_DE_JANEIRO");
        states.put("RN", "RIO_GRANDE_DO_NORTE");
        states.put("RO", "RONDONIA");
        states.put("RR", "RORAIMA");
        states.put("RS", "RIO_GRANDE_DO_SUL");
        states.put("SC", "SANTA_CATARINA");
        states.put("SE", "SERGIPE");
        states.put("SP", "SAO_PAULO");
        states.put("TO", "TOCANTINS");

        if (state.length() == 2){
            if (!checkClientSubscribeParseChannel(states.get(state))){
                setSubscribeParseChannel(states.get(state));
            }
        }
    }
}

