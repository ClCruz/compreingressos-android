//package br.com.compreingressos.broadcast;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.parse.ParseAnalytics;
//import com.parse.ParsePushBroadcastReceiver;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import br.com.compreingressos.CompreIngressosActivity;
//import br.com.compreingressos.MainActivity;
//
///**
// * Created by zaca on 5/11/15.
// */
//public class ParseReceiver extends ParsePushBroadcastReceiver {
//
//    private String title = "";
//    private String url = "";
//    private String codePromo = "";
//
//    public ParseReceiver() {
//        // TODO Auto-generated constructor stub
//    }
//
//    @Override
//    public void onPushOpen(Context context, Intent intent) {
//        Bundle extras = intent.getExtras();
//        ParseAnalytics.trackAppOpenedInBackground(intent);
//
//        String message = extras != null ? extras.getString("com.parse.Data") : "";
//        JSONObject jObject = null;
//
//        try {
//            jObject = new JSONObject(message);
//
//            if (jObject.has("alert"))
//                title = jObject.getString("alert");
//
//            if (jObject.has("u"))
//                url = jObject.getString("u");
//
//            if (jObject.has("c"))
//                codePromo = jObject.getString("c");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Intent i;
//
//        if (url == null || url.isEmpty()) {
//            i = new Intent(context, MainActivity.class);
//        } else {
//            i = new Intent(context, CompreIngressosActivity.class);
//            i.putExtras(intent.getExtras());
//            i.putExtra("titulo_espetaculo", title);
//            i.putExtra("u", url);
//            i.putExtra("c", codePromo);
//        }
//
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//
//
//    }
//
//
//}
