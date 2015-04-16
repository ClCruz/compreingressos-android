package br.com.compreingressos;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.sql.SQLException;
import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper =  new DatabaseHelper(this);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());

            orders = (ArrayList<Order>) orderDao.queryForAll();

            for (Order order : orders){
                Log.e(LOG_TAG, "order ------ > " + order.toString());

                for (Ingresso i : order.getIngressosCollection()){
                    Log.e(LOG_TAG, "Ingresso ------ > " + i.toString());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        OrderAdapter adapter = null;

        if (orders.size() > 0){




            adapter = new OrderAdapter(this, orders);
            adapter.setOnItemClickListener(onItemClick);
        }

        recyclerView.setAdapter(adapter);

    }

    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
//            Toast.makeText(getApplicationContext(), .get(position).getTitulo(), Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(EspetaculosActivity.this, CompreIngressosActivity.class);
//            intent.putExtra("url", espetaculos.get(position).getUrl());
//            startActivity(intent);

        }
    };
}
