package br.com.compreingressos;


import android.content.Intent;
import android.os.Bundle;
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

        getOrdersFromDatabase();

        adapter = new OrderAdapter(HistoryOrdersActivity.this, orders);

        initRecyclerView();
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
        requestQueue = VolleySingleton.getInstance(HistoryOrdersActivity.this).getRequestQueue();
        startRequest();

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
            public void onResponse(Order[] response) {

                if (response != null){
                    try {
                        orderDao.delete(orderDao.queryForAll());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < response.length; i++) {
                        Log.e(LOG_TAG, "---> " + response[i].toString());

                        try {

                            orderDao.create(response[i]);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }
                Log.e("dsdssdsdsd", "dsdsddssd - " + orders.size());

                    getOrdersFromDatabase();
                    initRecyclerView();
                    adapter.updateList(orders);

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

    private void startRequest() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        GsonRequest<Order[]> jsonObjRequest = new GsonRequest<>(Request.Method.GET, "http://tokecompre-ci.herokuapp.com/tickets.json?client_id="+ UserHelper.retrieveUserIdOnSharedPreferences(HistoryOrdersActivity.this), Order[].class, headers, this.createSuccessListener(), this.createErrorListener(), null);
        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjRequest);

    }

}
