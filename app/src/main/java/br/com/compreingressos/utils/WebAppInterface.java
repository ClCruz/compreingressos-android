package br.com.compreingressos.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.sql.SQLException;

import br.com.compreingressos.dao.OrderDao;
import br.com.compreingressos.helper.DatabaseHelper;
import br.com.compreingressos.helper.OrderHelper;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 17/03/15.
 */
public class WebAppInterface {
    public static final String LOG_TAG = "WebAppInterface";
    Context context;
    private OrderDao orderDao;
    private DatabaseHelper databaseHelper;

    public WebAppInterface(Context c){
        context = c;
    }

    @JavascriptInterface
    public void showToast(String toast){
        Log.e("--------REturn", "-------> " + toast);
//        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void getLogin(String user, String password){
        Log.e("-------- User ----", user.toString());
        Log.e("-------- Password ----", password.toString());
    }

    @JavascriptInterface
    public void getInfoPagamento(String resultJson){

        try {
            gravar(OrderHelper.loadOrderFromJSON(resultJson));
        } catch (SQLException e) {
            Log.e(LOG_TAG, "nao foi possivel salvar o pedido");
            e.printStackTrace();
        }
    }

    private boolean gravar(Order order) throws SQLException {
        databaseHelper =  new DatabaseHelper(context);

        try {
            orderDao = new OrderDao(databaseHelper.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        int x = 0;

        //create é o insert do objeto no bd, retorna  a qtd de linhas inseridas
        x = orderDao.create(order);

        return x > 0;
    }

}
