package br.com.compreingressos.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import br.com.compreingressos.model.Espetaculo;

/**
 * Created by luiszacheu on 14/04/15.
 */
public class EspetaculoDeserializer implements JsonDeserializer<Espetaculo> {

    private static final String LOG_TAG = "EspetaculoDeserializer";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Espetaculo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
        if (entry == null) return null;

        Date date;

        try {
            date = dateFormat.parse(((JsonObject)json).get("data").getAsString());

        }catch (ParseException e){
            e.printStackTrace();
            date =  null;
        }

        return new Espetaculo(obj.get("titulo").getAsString(),
                                obj.get("genero").getAsString(),
                                obj.get("nome_teatro").getAsString(),
                                obj.get("cidade").getAsString(),
                                obj.get("estado").getAsString(),
                                obj.get("miniatura").getAsString(),
                                obj.get("url").getAsString(),
                                date,
                                obj.get("relevancia").getAsInt());

    }
}
