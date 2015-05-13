package br.com.compreingressos.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.compreingressos.CompreIngressosActivity;
import br.com.compreingressos.MainActivity;

/**
 * Created by zaca on 5/11/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    public ParseReceiver() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        String url = "";
        String  title = "";
        Bundle extras = intent.getExtras();

        try {
            String message = extras != null ? extras.getString("com.parse.Data") : "";
            JSONObject jObject;

            jObject = new JSONObject(message);
            Log.e("Push", jObject.getString("alert"));
            Log.e("Push", jObject.getString("uri"));
            title = jObject.getString("alert");
            url = jObject.getString("uri");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.putExtra("titulo_espetaculo", title);
        i.putExtra("url", url);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }



}
