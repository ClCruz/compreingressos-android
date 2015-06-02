package br.com.compreingressos;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.compreingressos.adapter.OrderAdapter;
import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.helper.UserHelper;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.toolbox.GsonRequest;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class HistoryOrdersActivity extends ActionBarActivity {

    private static final String LOG_TAG  = "HistoryOrdersActivity";

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
            toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyHistory = (LinearLayout) findViewById(R.id.empty_history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryOrdersActivity.this));

        handler = new Handler();

        getOrdersFromDatabase();

        adapter = new OrderAdapter(HistoryOrdersActivity.this, orders);

        initRecyclerView();

        requestQueue = VolleySingleton.getInstance(HistoryOrdersActivity.this).getRequestQueue();
        startRequest();

    }

    private void getOrdersFromDatabase() {
        databaseHelper =  new DatabaseHelper(HistoryOrdersActivity.this);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            orders = orderDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        if (orders.size() > 0){

            adapter.setOnItemClickListener(onItemClick);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyHistory.setVisibility(View.GONE);
        }else{
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

                if (response != null){
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
                Log.e("-----> ", error.toString());
                progressDialog.dismiss();
            }
        };
    }

    private void startRequest() {
//        String envHomol = "&env=homol";

        progressDialog = new ProgressDialog(HistoryOrdersActivity.this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.show();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");


        GsonRequest<Order[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, "http://tokecompre-ci.herokuapp.com/tickets.json?os=android&client_id="+ UserHelper.retrieveUserIdOnSharedPreferences(HistoryOrdersActivity.this), Order[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);

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
                e.printStackTrace();
                return false;
            }

            for (int i = 0; i < params.length; i++) {
                try {
                    orderDao.create(params[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            getOrdersFromDatabase();
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean){
                adapter.updateList(orders);
                progressDialog.dismiss();
                initRecyclerView();
            }
        }
    }

}
