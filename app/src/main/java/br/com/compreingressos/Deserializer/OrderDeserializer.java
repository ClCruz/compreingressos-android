package br.com.compreingressos.deserializer;

import com.google.gson.JsonArray;
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

        if (!json.isJsonNull() && json.getAsJsonObject().has("number")){
            Date date;
            try {
                date = dateFormat.parse(((JsonObject)json).get("date").getAsString());
            }catch (ParseException e){
                date =  null;
            }
            Order order = new Order();
            Espetaculo espetaculo = new Espetaculo();

            order.setNumber(obj.get("number").getAsString());
            order.setTotal(obj.get("total").isJsonNull() ? "0" : (obj.get("total").getAsString()));
            order.setDate(date);
            espetaculo.setTitulo(obj.get("espetaculo").getAsJsonObject().get("titulo").isJsonNull() ? "" : obj.get("espetaculo").getAsJsonObject().get("titulo").getAsString());
            espetaculo.setTeatro(obj.get("espetaculo").getAsJsonObject().get("nome_teatro").isJsonNull() ? "" : obj.get("espetaculo").getAsJsonObject().get("nome_teatro").getAsString());
            espetaculo.setEndereco(obj.get("espetaculo").getAsJsonObject().get("endereco").isJsonNull() ? "" : obj.get("espetaculo").getAsJsonObject().get("endereco").getAsString());
            espetaculo.setHorario(obj.get("espetaculo").getAsJsonObject().get("horario").isJsonNull() ? "" : obj.get("espetaculo").getAsJsonObject().get("horario").getAsString());

            order.setEspetaculo(espetaculo);

            List<Ingresso> ingressos = new ArrayList<>();
            JsonArray ingressosArray = obj.getAsJsonArray("ingressos").getAsJsonArray();


            for (int i = 0; i < ingressosArray.size(); i++) {
                Ingresso ingresso = new Ingresso();

                ingresso.setQrcode(ingressosArray.get(i).getAsJsonObject().get("qrcode").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("qrcode").getAsString());
                ingresso.setLocal(ingressosArray.get(i).getAsJsonObject().get("local").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("local").getAsString());
                ingresso.setType(ingressosArray.get(i).getAsJsonObject().get("type").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("type").getAsString());
                ingresso.setPrice(ingressosArray.get(i).getAsJsonObject().get("price").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("price").getAsString());
                ingresso.setService_price(ingressosArray.get(i).getAsJsonObject().get("service_price").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("service_price").getAsString());
                ingresso.setTotal(ingressosArray.get(i).getAsJsonObject().get("total").isJsonNull() ? "" : ingressosArray.get(i).getAsJsonObject().get("total").getAsString());
                ingressos.add(ingresso);
            }

            order.setIngressos(ingressos);

            return order;
        }else{
            return null;
        }



    }
}
