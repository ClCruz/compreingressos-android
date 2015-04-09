package br.com.compreingressos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import br.com.compreingressos.utils.WebAppInterface;


public class CompreIngressosActivity extends ActionBarActivity {

    private static final String LOG_TAG = CompreIngressosActivity.class.getSimpleName();
//    private static final String URL_ESPETACULOS = "http://www.compreingressos.com/?app=tokecompre";
    private WebView webView;
    private String url;
    private String genero;

    private Toolbar toolbar;
    private boolean isFirstUrlLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compre_ingressos);

        if (getIntent().hasExtra("url")){
            url = getIntent().getStringExtra("url");
            genero = getIntent().getStringExtra("genero");
            Log.e(LOG_TAG, url);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar !=null){
            toolbar.setTitle(genero);
            toolbar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        webSettings.setGeolocationEnabled(true);



        webView.addJavascriptInterface(new WebAppInterface(this), "Android");


        webView.setWebViewClient(new WebViewClient() {

            ProgressDialog progressDialog;
            Intent intent = new Intent(CompreIngressosActivity.this, CompreIngressosActivity.class);

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        view.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.e("shouldOverrideKeyEvent", " keyevent -" + event);

                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                if (Uri.parse(url).getHost().equals("compra.compreingressos.com"))
//                    url = "http://186.237.201.132:81/compreingressos2/comprar/etapa1.php?apresentacao=61566&eventoDS=COSI%20FAN%20TUT%20TE";
//
//                if(!isFirstUrlLoading){
//                    intent.putExtra("url_flux_webview", url);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    return false;
//                }else{
//                    isFirstUrlLoading = false;
//                }
//                return false;

                Log.e(LOG_TAG, "url --> " + url);


                if (Uri.parse(url).getHost().equals("compra.compreingressos.com"))
                    url = "http://186.237.201.132:81/compreingressos2/comprar/etapa1.php?apresentacao=61566&eventoDS=COSI%20FAN%20TUT%20TE";

                if (url.contains("etapa1.php")){
                    view.setSupportZoom(true);
                }

                if (Uri.parse(url).getHost().equals("www.compreingressos.com") || Uri.parse(url).getHost().equals("compra.compreingressos.com") || Uri.parse(url).getHost().equals("186.237.201.132")) {
                    // This is my web site, so do not override; let my WebView load the page
                    view.loadUrl(url);
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
                return true;

            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Log.e("onPageStarted", "Iniciou");
                view.setVisibility(View.GONE);
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(CompreIngressosActivity.this);
                    progressDialog.setMessage("Aguarde...");
                    progressDialog.show();
                }
            }


            @Override
            public void onLoadResource(WebView view, String url) {
//                Log.e("onLoadResource", "Passou aqui");
                view.loadUrl("javascript:$(\"#menu_topo\").hide();$('.aba' && '.fechado').hide();$(\"#footer\").hide();$(\"#selos\").hide();");
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("onReceivedSslError", "aqui");
            }


        });

        if (getIntent().getStringExtra("url_flux_webview") == null){
            webView.loadUrl(url);
        }else{
            Log.e("link next activity", getIntent().getStringExtra("url_flux_webview"));
            webView.loadUrl(getIntent().getStringExtra("url_flux_webview"));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_compre_ingressos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                try {
                    NavUtils.navigateUpFromSameTask(this);
                }catch (Exception e){
                    onBackPressed();
                }
                return true;
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public void algo(){
        String x = "var date_aux = new Array; \n" +
                "$('.data').children().each(function(){date_aux.push($(this).html())}); \n" +
                "var order_date = date_aux.join(' '); \n" +
                "var spectacle_name = $('.resumo').find('.nome').html(); \n" +
                "var address = $('.resumo').find('.endereco').html(); \n" +
                "var theater = $('.resumo').find('.teatro').html(); \n" +
                "var time = $('.resumo').find('.horario').html(); \n" +
                "var order_number = $('.numero').find('a').html(); \n" +
                "var order_total = $('.pedido_total').find('.valor').html(); \n" +
                "var tickets = new Array; \n" +
                "$('tr').each(function() { \n" +
                "var qrcode = $(this).attr('data:uid'); \n" +
                "if (typeof qrcode !== typeof undefined && qrcode !== false) { \n" +
                "var local   = $(this).find('.local').find('td').html().replace('<br>', '').split('\\n').map(trim).join(' ').trim(); \n" +
                "var type    = $(this).find('.tipo').html(); \n" +
                "var aux     = $(this).find('td'); \n" +
                "var price   = aux.eq(3).children().eq(0).html(); \n" +
                "var service = aux.eq(4).html().replace('R$', ''); \n" +
                "var total   = aux.eq(5).children().eq(0).html(); \n" +
                "tickets.push({ \n" +
                "qrcode:        qrcode, \n" +
                "local:         local, \n" +
                "type:          type, \n" +
                "price:         price, \n" +
                "service_price: service, \n" +
                "total:         total \n" +
                "}); \n" +
                "} \n" +
                "}); \n" +
                "var payload = { \n" +
                "order: { \n" +
                "number: order_number, \n" +
                "date:   order_date, \n" +
                "total:  order_total, \n" +
                "espetaculo: { \n" +
                "titulo: spectacle_name, \n" +
                "endereco: address, \n" +
                "teatro: theater, \n" +
                "horario: time \n" +
                "}, \n" +
                "ingressos: tickets \n" +
                "} \n" +
                "}; \n" +
                "JSON.stringify(payload);";
    }




}
