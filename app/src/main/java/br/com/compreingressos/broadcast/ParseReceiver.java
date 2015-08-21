package br.com.compreingressos.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.compreingressos.CompreIngressosActivity;
import br.com.compreingressos.MainActivity;

/**
 * Created by zaca on 5/11/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    String title = "";
    String url = "";
    String codePromo = "";

    public ParseReceiver() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onPushOpen(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        ParseAnalytics.trackAppOpenedInBackground(intent);

        try {
            String message = extras != null ? extras.getString("com.parse.Data") : "";
            JSONObject jObject;

            jObject = new JSONObject(message);
            title = jObject.getString("alert");
            try {
                url = jObject.getString("u");
            }catch (Exception e){
                Crashlytics.logException(e);
                Crashlytics.log(Log.ERROR, ParseReceiver.class.getSimpleName(), "url -> " + url);
                url = null;
                e.printStackTrace();
            }

            try {
                codePromo = jObject.getString("c");
            }catch (Exception e){
                Crashlytics.logException(e);
                Crashlytics.log(Log.ERROR, ParseReceiver.class.getSimpleName(), "codePromo -> " + codePromo);
                codePromo = null;
                e.printStackTrace();
            }

            Intent i;

            if (url != null){
                i = new Intent(context, CompreIngressosActivity.class);
                i.putExtras(intent.getExtras());
                i.putExtra("titulo_espetaculo", title);
                i.putExtra("u", url);
                i.putExtra("c", codePromo);
            }else{
                i = new Intent(context, MainActivity.class);
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        } catch (JSONException e) {
            Crashlytics.log(extras.getString("com.parse.Data"));
            Crashlytics.logException(e);
        } catch (NullPointerException e){
            Crashlytics.log(extras.getString("com.parse.Data"));
            Crashlytics.logException(e);
        } catch (Exception e){
            Crashlytics.log(extras.getString("com.parse.Data"));
            Crashlytics.logException(e);
        }


    }



}
