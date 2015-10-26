package br.com.compreingressos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.Dialogs;


public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 4000;
    private static String TAG_LOG = SplashScreenActivity.class.getSimpleName();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        this.requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        startRequest();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void checkForceUpdate(){
        Dialogs.showDialogForceUpdate(getApplicationContext(), SplashScreenActivity.this, getString(R.string.force_update_dialog_text), getString(R.string.force_update_dialog_title), getApplicationContext().getPackageName());
    }


    private void startRequest(){
        String versionName = BuildConfig.VERSION_NAME;
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest("http://tokecompre-ci.herokuapp.com/force_update?version="+ versionName +"&os=android", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean has_update = response.getBoolean("force_update");
                    if (has_update){
                        checkForceUpdate();
                    }else{
                        initSplash();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                initSplash();
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void initSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

}

