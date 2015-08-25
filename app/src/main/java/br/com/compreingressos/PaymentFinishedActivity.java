package br.com.compreingressos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.helper.ParseHelper;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by luiszacheu on 28/04/15.
 */
public class PaymentFinishedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnVerIngressos;
    private DatabaseHelper databaseHelper;
    private OrderDao orderDao;
    private Order order;
    private Boolean hasAssinatura = false;
    private RequestQueue requestQueue;
    private boolean hasSendTrackPurchase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_finished);

        if (getIntent().hasExtra("assinatura"))
            hasAssinatura = getIntent().getBooleanExtra("assinatura", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle("");
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);

            if (Build.VERSION.SDK_INT >= 21) {
                this.setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark);
                toolbar.setBackgroundColor(getResources().getColor(R.color.red_compreingressos));
                getWindow().setStatusBarColor(getResources().getColor(R.color.red_status_bar));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close_white));
            }else{
                toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
                getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close));
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        Tracker mTracker = ((CompreIngressosApplication) getApplication()).getTracker();
        mTracker.setScreenName(ConstantsGoogleAnalytics.FINALIZACAO_PAGAMENTO);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        databaseHelper =  new DatabaseHelper(PaymentFinishedActivity.this);

        btnVerIngressos = (Button) findViewById(R.id.btn_ver_ingressos);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            QueryBuilder<Order, Integer> qb = orderDao.queryBuilder();
            qb.orderBy("id", false);
            order = orderDao.queryForFirst(qb.prepare());
            order.setIngressos(new ArrayList<>(order.getIngressosCollection()));

        } catch (SQLException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        if (!ParseHelper.getIsClient(PaymentFinishedActivity.this)){
            ParseHelper.setIsClient(PaymentFinishedActivity.this);
            ParseHelper.setUnSubscribeParseChannel("prospect");
            ParseHelper.setSubscribeParseChannel("client");
        }

        requestQueue = VolleySingleton.getInstance(PaymentFinishedActivity.this).getRequestQueue();
        try {
            if (hasSendTrackPurchase == false){
                startRequest(order);
            }

        } catch (JSONException e) {
            Crashlytics.logException(e);
            Crashlytics.log(Log.ERROR, PaymentFinishedActivity.class.getSimpleName(), "order -> " + order.toString());
            e.printStackTrace();
        }

        btnVerIngressos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentFinishedActivity.this, DetailHistoryOrderActivity.class);
                intent.putExtra("isFinishedOrder", true);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

        if (hasAssinatura)
            btnVerIngressos.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent homeIntent = new Intent(PaymentFinishedActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Response.Listener<JSONObject> createSuccessListener() {
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                if (response != null){
                    hasSendTrackPurchase = true;
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("-----> ", error.toString());
            }
        };
    }


    private void startRequest(Order order) throws JSONException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("number", order.getNumber());
        jsonBody.put("total", order.getTotal());

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, "http://tokecompre-ci.herokuapp.com/track_purchases.json?os=android", jsonBody, this.createSuccessListener(), this.createErrorListener());
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);

    }
}
