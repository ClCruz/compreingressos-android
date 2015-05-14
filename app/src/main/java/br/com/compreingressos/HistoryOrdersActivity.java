package br.com.compreingressos;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.adapter.OrderAdapter;
import br.com.compreingressos.dao.OrderDao;

import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class HistoryOrdersActivity extends ActionBarActivity {

    private static final String LOG_TAG  = "HistoryOrdersActivity";

    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderDao orderDao;
    private ArrayList<Order> orders = null;
    private LinearLayout emptyHistory;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper =  new DatabaseHelper(this);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            orders = (ArrayList<Order>) orderDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        OrderAdapter adapter = null;

        if (orders.size() > 0){
            adapter = new OrderAdapter(this, orders);
            adapter.setOnItemClickListener(onItemClick);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptyHistory.setVisibility(View.VISIBLE);
        }



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

}
