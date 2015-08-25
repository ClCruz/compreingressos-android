package br.com.compreingressos;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compreingressos.adapter.OrderAdapter;
import br.com.compreingressos.contants.ConstantsGoogleAnalytics;
import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.helper.UserHelper;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.ConnectionUtils;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class HistoryOrdersActivity extends AppCompatActivity {

    private static final String LOG_TAG = "HistoryOrdersActivity";

    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderDao orderDao;
    private List<Order> orders = Collections.emptyList();
    private LinearLayout emptyHistory;
    private OrderAdapter adapter;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Historico de Pedidos");
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);

            if (Build.VERSION.SDK_INT >= 21) {
                this.setTheme(R.style.Base_ThemeOverlay_AppCompat_Dark);
                toolbar.setBackgroundColor(getResources().getColor(R.color.red_compreingressos));
                getWindow().setStatusBarColor(getResources().getColor(R.color.red_status_bar));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            }else{
                toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Tracker mTracker = ((CompreIngressosApplication) getApplication()).getTracker();
        mTracker.setScreenName(ConstantsGoogleAnalytics.MEUS_INGRESSOS);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        emptyHistory = (LinearLayout) findViewById(R.id.empty_history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryOrdersActivity.this));

        handler = new Handler();

        getOrdersFromDatabase();

        adapter = new OrderAdapter(HistoryOrdersActivity.this, orders);

        initRecyclerView();

        if (ConnectionUtils.isInternetOn(HistoryOrdersActivity.this)) {
            requestQueue = VolleySingleton.getInstance(HistoryOrdersActivity.this).getRequestQueue();
            startRequest();
        } else {
            Toast.makeText(HistoryOrdersActivity.this, getResources().getString(R.string.message_sem_conexao), Toast.LENGTH_LONG).show();
        }

    }

    private void getOrdersFromDatabase() {
        databaseHelper = new DatabaseHelper(HistoryOrdersActivity.this);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            orders = orderDao.queryForAll();
        } catch (SQLException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        if (orders.size() > 0) {

            adapter.setOnItemClickListener(onItemClick);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyHistory.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyHistory.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(HistoryOrdersActivity.this, DetailHistoryOrderActivity.class);

            Order order = orders.get(position);
            order.setIngressos(new ArrayList<>(order.getIngressosCollection()));

            intent.putExtra("order", orders.get(position));

            startActivity(intent);

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Response.Listener<Order[]> createSuccessListener() {
        return new Response.Listener<Order[]>() {

            @Override
            public void onResponse(final Order[] response) {

                if (response != null) {
                    SaveOnDabaseAsyncTask saveOnDabaseAsyncTask = new SaveOnDabaseAsyncTask();
                    saveOnDabaseAsyncTask.execute(response);

                }

            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        };
    }

    private void startRequest() {
        String clientId = UserHelper.retrieveUserIdOnSharedPreferences(HistoryOrdersActivity.this);
        if (clientId.trim().length() > 0) {
            String envHomol = "";
            if (CompreIngressosApplication.isRunnigOnEnvironmentDevelopment())
                envHomol = "&env=homol";

            progressDialog = new ProgressDialog(HistoryOrdersActivity.this);
            progressDialog.setMessage("Aguarde...");
            progressDialog.show();
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");

            GsonRequest<Order[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, "http://tokecompre-ci.herokuapp.com/tickets.json?os=android" + envHomol + "&client_id=" + clientId, Order[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
            jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(jsonObjRequest);
        }

    }


    public class SaveOnDabaseAsyncTask extends AsyncTask<Order, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Order... params) {

            try {
                orderDao.delete(orderDao.queryForAll());
            } catch (SQLException e) {
                Crashlytics.log(Log.ERROR, HistoryOrdersActivity.class.getSimpleName() + "(doInBackground)", params.toString());
                Crashlytics.logException(e);
                e.printStackTrace();
                return false;
            }

            for (int i = 0; i < params.length; i++) {
                try {
                    orderDao.create(params[i]);
                } catch (SQLException e) {
                    Crashlytics.log(Log.ERROR, HistoryOrdersActivity.class.getSimpleName() + "(doInBackground)", params[i].toString());
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    return false;
                }
            }

            getOrdersFromDatabase();
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {
                adapter.updateList(orders);
                progressDialog.dismiss();
                initRecyclerView();
            }
        }
    }

}
