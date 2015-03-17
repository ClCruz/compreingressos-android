package br.com.compreingressos.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }


}
