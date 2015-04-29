package br.com.compreingressos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 28/04/15.
 */
public class PaymentFinishedActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private Button btnVerIngressos;
    private DatabaseHelper databaseHelper;
    private OrderDao orderDao;
    private Order order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_finished);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle("");
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_action_close));

        }

        databaseHelper =  new DatabaseHelper(this);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
            QueryBuilder<Order, Integer> qb = orderDao.queryBuilder();
            qb.orderBy("id", false);
            order = orderDao.queryForFirst(qb.prepare());
            int id = -1;
            if (order == null){
                id = -1;
            }else{
                id = order.getId();
            }
            order.setIngressos(new ArrayList<>(order.getIngressosCollection()));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnVerIngressos = (Button) findViewById(R.id.btn_ver_ingressos);
        btnVerIngressos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PaymentFinishedActivity.this, DetailHistoryOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("isFinishedOrder", true);
                intent.putExtra("order", order);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent homeIntent = new Intent(PaymentFinishedActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
