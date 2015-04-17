package br.com.compreingressos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.adapter.OrderDetailAdapter;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class DetailHistoryOrderActivity extends ActionBarActivity {

    private static final String LOG_TAG = "DetailHistoryOrderActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderDetailAdapter adapter;

    private Order order;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_order);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        intent = getIntent();
        if (intent.getSerializableExtra("order") != null){
            order = (Order) intent.getSerializableExtra("order");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tickts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderDetailAdapter(this, order);

        recyclerView.setAdapter(adapter);

    }
}
