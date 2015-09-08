package br.com.compreingressos;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.session.SessionManager;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by zaca on 9/4/15.
 */
public class LoginActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private SessionManager session;
    EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.email);
        edtPassword = (EditText) findViewById(R.id.password);

        session = new SessionManager(getApplicationContext());
        requestQueue = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();

    }

    public void SignIn(View v){
        try {
            startRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<String> createSuccessListener() {
        return new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("---------", response);
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener
                () {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    public void startRequest() throws JSONException {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        JSONObject userJson = new JSONObject();
        userJson.accumulate("email", email).accumulate("password", password);

        Log.e("------->> ", userJson.toString());

        if (email.trim().length() > 0 && password.trim().length() > 0) {
            GsonRequest<String> gsonRequest = new GsonRequest<>(Request.Method.POST, "http://tokecompre-ci.herokuapp.com/sessions", String.class, headers, createSuccessListener(), createErrorListener(), null, userJson);
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(gsonRequest);
        } else {
            Snackbar.make(viewGroup, "Verifique se os dados informados", Snackbar.LENGTH_SHORT).show();
        }

    }

}

