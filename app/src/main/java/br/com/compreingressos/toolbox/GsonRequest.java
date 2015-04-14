package br.com.compreingressos.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import br.com.compreingressos.utils.DateDeserializer;
import br.com.compreingressos.model.Espetaculo;

/**
 * Created by luiszacheu on 02/04/15.
 */
public class GsonRequest<T> extends Request<T> {

    private static final String LOG_TAG = "GsonRequest";

    private final GsonBuilder gsonBuilder = new GsonBuilder();

    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private String formatString =  "";

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener, String formatString) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.formatString = formatString == null ? "" : formatString;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,HttpHeaderParser.parseCharset(response.headers));



            if (!formatString.isEmpty()){
                gsonBuilder.registerTypeAdapter(Espetaculo.class, new DateDeserializer());
                gsonBuilder.serializeNulls();
            }

            Gson gson = formatString.isEmpty() ? new Gson() : gsonBuilder.create();

            return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}