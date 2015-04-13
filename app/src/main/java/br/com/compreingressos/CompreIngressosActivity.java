package br.com.compreingressos;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
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
import android.widget.Button;

import br.com.compreingressos.utils.WebAppInterface;


public class CompreIngressosActivity extends ActionBarActivity {

    private static final String LOG_TAG = CompreIngressosActivity.class.getSimpleName();
//    private static final String URL_ESPETACULOS = "http://www.compreingressos.com/?app=tokecompre";
    private WebView webView;
    private String url;
    private String genero;

    private Toolbar toolbar;
    private Button btnAvancar;
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

        btnAvancar = (Button) findViewById(R.id.btn_avancar);

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

                if (url.contains("pagamento_ok.php")){
                    view.loadUrl(runScripGetInfoPayment());
                }

            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.e("shouldOverrideKeyEvent", " keyevent -" + event);

                return super.shouldOverrideKeyEvent(view, event);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                Log.e(LOG_TAG, "url --> " + url);

                if (Uri.parse(url).getHost().equals("compra.compreingressos.com"))
                    url = "http://186.237.201.132:81/compreingressos2/comprar/etapa1.php?apresentacao=61566&eventoDS=COSI%20FAN%20TUT%20TE";
                if (url.contains("etapa1.php")){
                    Log.e(LOG_TAG, "Entrou aqui");
//                    view.loadUrl("javascript:$('.container_botoes_etapas').hide()");
                    WebSettings webSettings = view.getSettings();
                    webSettings.setBuiltInZoomControls(true);
                    webSettings.setDisplayZoomControls(false);

                    btnAvancar.setVisibility(View.VISIBLE);
                    btnAvancar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StringBuilder scriptOnClick  = new StringBuilder("javascript:var length = $('.container_botoes_etapas').find('a').length;");
                            scriptOnClick.append("$('.container_botoes_etapas').find('a')[length - 1].click(); ");
                            Log.e(LOG_TAG, scriptOnClick.toString());
                            view.loadUrl(scriptOnClick.toString());
                        }
                    });

                }

                if (Uri.parse(url).getHost().equals("www.compreingressos.com") || Uri.parse(url).getHost().equals("compra.compreingressos.com") || Uri.parse(url).getHost().equals("186.237.201.132")) {
                    view.loadUrl(url);
                    return false;
                }

                return true;

            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.setVisibility(View.GONE);
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(CompreIngressosActivity.this);
                    progressDialog.setMessage("Aguarde...");
                    progressDialog.show();
                }


            }


            @Override
            public void onLoadResource(WebView view, String url) {
//                Log.e("onLoadResource", "Passou aqui");
                view.loadUrl("javascript:$(\"#menu_topo\").hide();$('.aba' && '.fechado').hide();$(\"#footer\").hide();$(\"#selos\").hide();");
                view.loadUrl("javascript:$(document).ready(function(){$('.container_botoes_etapas').hide();});");

                if (url.contains("etapa1.php")){
                    view.loadUrl("javascript:Android.showToast($(\".destaque_menor_v2\").first().find(\"h3\").text());");

                }
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

    public String runScripGetInfoPayment(){
        StringBuilder scriptGetInfoPayment = new StringBuilder("javascript:var date_aux = new Array; \n");
        scriptGetInfoPayment.append("$('.data').children().each(function(){date_aux.push($(this).html())}); \n");
        scriptGetInfoPayment.append("var order_date = date_aux.join(' '); \n");
        scriptGetInfoPayment.append("var spectacle_name = $('.resumo').find('.nome').html(); \n");
        scriptGetInfoPayment.append("var address = $('.resumo').find('.endereco').html(); \n");
        scriptGetInfoPayment.append("var theater = $('.resumo').find('.teatro').html(); \n");
        scriptGetInfoPayment.append("var time = $('.resumo').find('.horario').html(); \n");
        scriptGetInfoPayment.append("var order_number = $('.numero').find('a').html(); \n");
        scriptGetInfoPayment.append("var order_total = $('.pedido_total').find('.valor').html(); \n");
        scriptGetInfoPayment.append("var tickets = new Array; \n");
        scriptGetInfoPayment.append("$('tr').each(function() { \n");
        scriptGetInfoPayment.append("var qrcode = $(this).attr('data:uid'); \n");
        scriptGetInfoPayment.append("if (typeof qrcode !== typeof undefined && qrcode !== false) { \n");
        scriptGetInfoPayment.append("var local   = $(this).find('.local').find('td').html().replace('<br>', '').split('\\n').map(trim).join(' ').trim(); \n");
        scriptGetInfoPayment.append("var type    = $(this).find('.tipo').html(); \n");
        scriptGetInfoPayment.append("var aux     = $(this).find('td'); \n");
        scriptGetInfoPayment.append("var price   = aux.eq(3).children().eq(0).html(); \n");
        scriptGetInfoPayment.append("var service = aux.eq(4).html().replace('R$', ''); \n");
        scriptGetInfoPayment.append("var total   = aux.eq(5).children().eq(0).html(); \n");
        scriptGetInfoPayment.append("tickets.push({ \n");
        scriptGetInfoPayment.append("qrcode:        qrcode, \n");
        scriptGetInfoPayment.append("local:         local, \n");
        scriptGetInfoPayment.append("type:          type, \n");
        scriptGetInfoPayment.append("price:         price, \n");
        scriptGetInfoPayment.append("service_price: service, \n");
        scriptGetInfoPayment.append("total:         total \n");
        scriptGetInfoPayment.append("}); \n");
        scriptGetInfoPayment.append("} \n");
        scriptGetInfoPayment.append("}); \n");
        scriptGetInfoPayment.append("var payload = { \n");
        scriptGetInfoPayment.append("order: { \n");
        scriptGetInfoPayment.append("number: order_number, \n");
        scriptGetInfoPayment.append("date:   order_date, \n");
        scriptGetInfoPayment.append("total:  order_total, \n");
        scriptGetInfoPayment.append("espetaculo: { \n");
        scriptGetInfoPayment.append("titulo: spectacle_name, \n");
        scriptGetInfoPayment.append("endereco: address, \n");
        scriptGetInfoPayment.append("nome_teatro: theater, \n");
        scriptGetInfoPayment.append("horario: time \n");
        scriptGetInfoPayment.append("}, \n");
        scriptGetInfoPayment.append("ingressos: tickets \n");
        scriptGetInfoPayment.append("} \n");
        scriptGetInfoPayment.append("}; \n");
        scriptGetInfoPayment.append("Android.getInfoPagamento(JSON.stringify(payload));");

        Log.e(LOG_TAG, "scrip get info " + scriptGetInfoPayment.toString());

        return scriptGetInfoPayment.toString();
    }




}
