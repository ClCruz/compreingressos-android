package br.com.compreingressos.toolbox;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import br.com.compreingressos.deserializer.EspetaculoDeserializer;
import br.com.compreingressos.deserializer.OrderDeserializer;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 02/04/15.
 */
public class GsonRequest<T> extends Request<T> {

    private static final String LOG_TAG = "GsonRequest";

    private final GsonBuilder gsonBuilder = new GsonBuilder();

    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private String formatString = "";
    private JSONObject params = null;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
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

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener, String formatString, JSONObject params) {
        this(method, url, clazz, headers, listener, errorListener, formatString);
        this.params = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return params.toString().getBytes(getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return null;
    }


    @Override
    protected void deliverResponse(T response)
    {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String json = null;

        try {
            json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            gsonBuilder.registerTypeAdapter(Espetaculo.class, new EspetaculoDeserializer());
            gsonBuilder.registerTypeAdapter(Order.class, new OrderDeserializer());
            gsonBuilder.serializeNulls();

            Gson gson = gsonBuilder.create();

            return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Crashlytics.logException(e);
            Crashlytics.log(Log.ERROR, "GsonRequest(UnsupportedEncodingException)", "json -> " + json);
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Crashlytics.log(Log.ERROR, "GsonRequest(JsonSyntaxException)", "json -> " + json);
            Crashlytics.logException(e);
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "GsonRequest(Exception)", "json -> " + json);
            Crashlytics.logException(e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return super.getRetryPolicy();
    }
}