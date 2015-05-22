package br.com.compreingressos.deserializer;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by zaca on 5/12/15.
 */
public class OrderDeserializer implements JsonDeserializer<Order> {

    private static final String LOG_TAG = "OrderDeserializer";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", new Locale("pt", "BR"));

    @Override
    public Order deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
        if (entry == null) return null;

        Date date;

        try {
            date = dateFormat.parse(((JsonObject)json).get("date").getAsString());

        }catch (ParseException e){
            e.printStackTrace();
            date =  null;
        }
        Order order = new Order();
        Espetaculo espetaculo = new Espetaculo();

        order.setNumber(obj.get("number").getAsString());
        order.setDate(date);
        order.setTotal(obj.get("total").getAsString());

        espetaculo.setTitulo(obj.get("espetaculo").getAsJsonObject().get("titulo").getAsString());
        espetaculo.setTeatro(obj.get("espetaculo").getAsJsonObject().get("nome_teatro").getAsString());
        espetaculo.setEndereco(obj.get("espetaculo").getAsJsonObject().get("endereco").getAsString());
        espetaculo.setHorario(obj.get("espetaculo").getAsJsonObject().get("horario").getAsString());

        order.setEspetaculo(espetaculo);

        List<Ingresso> ingressos = new ArrayList<>();

        for (int i = 0; i < obj.getAsJsonArray("ingressos").getAsJsonArray().size(); i++) {
            Ingresso ingresso = new Ingresso();

            ingresso.setQrcode(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("qrcode").getAsString());
            ingresso.setLocal(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("local").getAsString());
            ingresso.setType(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("type").getAsString());
            ingresso.setPrice(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("price").getAsString());
            ingresso.setService_price(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("service_price").getAsString());
            ingresso.setTotal(obj.getAsJsonArray("ingressos").get(i).getAsJsonObject().get("total").getAsString());

            ingressos.add(ingresso);
        }

        order.setIngressos(ingressos);

        return order;
    }
}
