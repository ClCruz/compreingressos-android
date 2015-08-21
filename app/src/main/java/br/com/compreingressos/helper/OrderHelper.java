package br.com.compreingressos.helper;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.compreingressos.deserializer.OrderDeserializer;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 13/04/15.
 */
public class OrderHelper {

    private static final String LOG_TAG = "OrderHelper";
    public static final String JSON = " {\"number\":\"436464\",\"date\":\"sáb 28 nov\",\"total\":\"50,00\",\"espetaculo\":{\"titulo\":\"COSI FAN TUT TE\",\"endereco\":\"Praça Ramos de Azevedo, s/n - República - São Paulo, SP\",\"nome_teatro\n\":\"Theatro Municipal de São Paulo\",\"horario\":\"20h00\"},\"ingressos\":[{\"qrcode\":\"0054741128200000100146\",\"local\":\"SETOR 3 ANFITEATRO C-06\",\"type\":\"INTEIRA\",\"price\":\"50,00\",\"service_price\":\" 0,00\",\"total\":\"50,00\"}]}";


    public static Order loadOrderFromJSON(String jsonString){
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Order.class, new OrderDeserializer());
        Gson gson = gsonBuilder.create();

            try {
                return gson.fromJson(jsonString, Order.class);
            }catch (Exception e ){
                e.printStackTrace();
                Crashlytics.logException(e);
                Crashlytics.log("jsonString -> \n " + jsonString);
                return  null;
            }
    }

    public static String createJsonPeerTicket(Order order, Ingresso ingresso){
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");

        JsonObject jsonOrder = new JsonObject();

        jsonOrder.addProperty("number", order.getNumber());
        jsonOrder.addProperty("date", sdf.format(order.getDate()));
        jsonOrder.addProperty("total", order.getTotal());

        JsonObject jsonEspetaculo = new JsonObject();
        jsonEspetaculo.addProperty("titulo", order.getTituloEspetaculo());
        jsonEspetaculo.addProperty("endereco", order.getEnderecoEspetaculo());
        jsonEspetaculo.addProperty("nome_teatro", order.getNomeTeatroEspetaculo());
        jsonEspetaculo.addProperty("horario", order.getHorarioEspetaculo());

        JsonArray jsonIngressos = new JsonArray();
        JsonObject jsonIngresso = new JsonObject();
        jsonIngresso.addProperty("qrcode", ingresso.getQrcode());
        jsonIngresso.addProperty("local", ingresso.getLocal());
        jsonIngresso.addProperty("type", ingresso.getType());
        jsonIngresso.addProperty("price", ingresso.getPrice());
        jsonIngresso.addProperty("service_price", ingresso.getService_price());
        jsonIngresso.addProperty("total", ingresso.getTotal());

        jsonIngressos.add(jsonIngresso);

        jsonOrder.add("espetaculo", jsonEspetaculo);
        jsonOrder.add("ingressos", jsonIngressos);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        return gson.toJson(jsonOrder);
    }
}
