package br.com.compreingressos.helper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;

import br.com.compreingressos.deserializer.OrderDeserializer;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 13/04/15.
 */
public class OrderHelper {

    private static final String LOG_TAG = "OrderHelper";
    public static final String JSON = "{\"number\":\"436821\",\"date\":\"ter 20 out\",\"total\":\"50,00\",\"espetaculo\":{\"titulo\":\"LOHENGRIN\",\"endereco\":\"Praça Ramos de Azevedo, s/n - República - São Paulo, SP\",\"nome_teatro\":\"Theatro Municipal de São Paulo\",\"horario\":\"20h00\"},\"ingressos\":[{\"qrcode\":\"0052421020200000100137\",\"local\":\"SETOR 3 BALCÃO SIMPLES A-24\",\"type\":\"INTEIRA\",\"price\":\"50,00\",\"service_price\":\" 0,00\",\"total\":\"50,00\"}]}";


    public static Order loadOrderFromJSON(String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Order.class, new OrderDeserializer());
        Gson gson = gsonBuilder.create();

        try {
            return gson.fromJson(jsonString, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonPeerTicket(Order order, Ingresso ingresso) {
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
