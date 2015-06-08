package br.com.compreingressos.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
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
        Log.e("Push", "Clicked");

        try {
            Bundle extras = intent.getExtras();

            String message = extras != null ? extras.getString("com.parse.Data") : "";
            JSONObject jObject;

            jObject = new JSONObject(message);
            Log.e("Push", jObject.getString("alert"));
            Log.e("Push", jObject.getString("u"));
            Log.e("Push", jObject.getString("c"));
            title = jObject.getString("alert");
            try {
                url = jObject.getString("u");
            }catch (Exception e){
                url = null;
                e.printStackTrace();
            }

            try {
                codePromo = jObject.getString("c");
            }catch (Exception e){
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
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (NullPointerException e){
            Crashlytics.logException(e);
        }


    }



}
