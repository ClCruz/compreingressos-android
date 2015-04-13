package br.com.compreingressos.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import br.com.compreingressos.helper.OrderHelper;

/**
 * Created by luiszacheu on 17/03/15.
 */
public class WebAppInterface {
    Context context;

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
        Log.e("json result ------->", "" + resultJson);
        OrderHelper.loadOrderFromJSON(resultJson);
    }

}
