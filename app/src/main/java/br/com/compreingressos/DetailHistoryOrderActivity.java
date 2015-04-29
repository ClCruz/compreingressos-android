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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.adapter.GeneroAdapter;
import br.com.compreingressos.adapter.OrderDetailAdapter;
import br.com.compreingressos.interfaces.OnItemClickListener;
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


    private boolean isFinishedOrder = false;
    private Order order;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_order);

        intent = getIntent();
        if (intent.getSerializableExtra("order") != null){
            order = (Order) intent.getSerializableExtra("order");
        }

        isFinishedOrder = intent.getBooleanExtra("isFinishedOrder", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Meus Ingressos");
            toolbar.setTitleTextColor(getResources().getColor(R.color.red_compreingressos));
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            if (isFinishedOrder){
                getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close));
            }else{
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tickts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderDetailAdapter(this, order);

        recyclerView.setAdapter(adapter);

    }

    private OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isFinishedOrder){
                    Intent homeIntent = new Intent(DetailHistoryOrderActivity.this, MainActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    finish();
                }else{
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
