package br.com.compreingressos.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 13/04/15.
 */
public class OrderHelper {

    private static final String LOG_TAG = "OrderHelper";
    public static final String JSON = " {\"number\":\"436464\",\"date\":\"sáb 28 nov\",\"total\":\"50,00\",\"espetaculo\":{\"titulo\":\"COSI FAN TUT TE\",\"endereco\":\"Praça Ramos de Azevedo, s/n - República - São Paulo, SP\",\"teatro\":\"Theatro Municipal de São Paulo\",\"horario\":\"20h00\"},\"ingressos\":[{\"qrcode\":\"0054741128200000100146\",\"local\":\"SETOR 3 ANFITEATRO C-06\",\"type\":\"INTEIRA\",\"price\":\"50,00\",\"service_price\":\" 0,00\",\"total\":\"50,00\"}]}";

    public static Order loadOrderFromJSON(String jsonString){
        Gson gson = new Gson();

        try {

            Log.e(LOG_TAG, "jsonString -> " + jsonString);

            Order order = gson.fromJson(jsonString, Order.class);


            Log.e(LOG_TAG, "Order -> " + order.toString());

            for (Ingresso i : order.getIngressos()){
                Log.e(LOG_TAG, i.toString());
            }

            return order;
        }catch (JsonSyntaxException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }catch (JsonParseException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }



    }
}
